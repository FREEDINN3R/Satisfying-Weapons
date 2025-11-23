package net.freedinner.satisfying_weapons.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.freedinner.satisfying_weapons.util.data.ILivingEntityDataSaver;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class GlassSwordItem extends UpgradeableSwordItem implements FabricItem {
    private static final double SHATTER_EFFECT_RADIUS = 4;
    private static final String GLASS_STATE_NBT_KEY = "glass_state";

    private static final Multimap<EntityAttribute, EntityAttributeModifier> brokenAttributeModifiers;

    static {
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                        ATTACK_DAMAGE_MODIFIER_ID,
                        "Custom damage modifier",
                        1,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                        ATTACK_SPEED_MODIFIER_ID,
                        "Custom speed modifier",
                        -0.8,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );

        brokenAttributeModifiers = builder.build();
    }

    public GlassSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable GlassSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int swordLevel = ((GlassSwordItem) stack.getItem()).getLevel();

        if (shouldApplyGlassCut(attacker, swordLevel, getGlassState(stack))) {
            addGlassCutStack(target, 400, swordLevel);
        }

        if (isCrackedSword(stack) || isBrokenSword(stack)) {
             return true; // skipping durability and other checks
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return switch (getGlassState(stack)) {
            case INTACT -> super.getTranslationKey(stack);
            case CRACKED -> "item.satisfying_weapons.cracked_glass_sword";
            case BROKEN -> "item.satisfying_weapons.broken_glass_sword";
        };
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient) {
            return;
        }

        // A very ugly fix for a very annoying but rare problem
        // Try to guess what it does lol
        if (!stack.getOrCreateNbt().contains("glass_sword_serial_number")) {
            stack.getOrCreateNbt().putInt("glass_sword_serial_number", MathUtils.randomNumber(10000));
        }

        if (!isCrackedSword(stack)) {
            return;
        }

        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.getStackInHand(Hand.MAIN_HAND) != stack
        || !livingEntity.isAlive() || !livingEntity.hasStatusEffect(ModEffects.BROKEN_SOUL)) {
            GlassSwordItem.setGlassState(stack, State.BROKEN);
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return super.isItemBarVisible(stack) && isIntactSword(stack);
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        ItemStack oldStackCopy = oldStack.copy();
        setGlassState(oldStackCopy, getGlassState(newStack));

        return !ItemStack.areEqual(oldStackCopy, newStack); // Cancels update animation if only glass state has changed
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (isBrokenSword(stack) && slot == EquipmentSlot.MAINHAND) {
            return brokenAttributeModifiers;
        }

        return super.getAttributeModifiers(stack, slot);
    }

    // This is the main "revive" method, called at any sword lvl
    // Branches into actuallyShatter() or applying Broken Soul first
    public static boolean trySaveFromDeath(LivingEntity swordHolder) {
        ItemStack itemStack = swordHolder.getStackInHand(Hand.MAIN_HAND);

        if (!isIntactSword(itemStack)) {
            return false;
        }

        // Reviving starts here
        swordHolder.clearStatusEffects();
        swordHolder.extinguish();
        swordHolder.setHealth(1f);

        int swordLevel = ((GlassSwordItem) itemStack.getItem()).getLevel();

        if (swordLevel <= 2) {
            // Just shatter immediately
            setGlassState(itemStack, State.BROKEN);
            actuallyShatter(swordHolder, swordLevel);
        }
        else {
            // Apply Broken Soul first
            setGlassState(itemStack, State.CRACKED);

            swordHolder.addStatusEffect(new StatusEffectInstance(ModEffects.BROKEN_SOUL, 240, 0, false, false, true));
            ((ILivingEntityDataSaver) swordHolder).sw$setBrokenSoulSwordLevel(swordLevel); // Saving lvl to apply GC properly

            swordHolder.getWorld().playSound(null, swordHolder.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, PitchUtils.get() + 0.3f);
        }

        return true;
    }

    // This one is called when the sword actually shatters, applies GC to all nearby entities
    public static void actuallyShatter(LivingEntity entity, int swordLevel) {
        Vec3d pos = entity.getPos();
        Box box = new Box(pos, pos).expand(SHATTER_EFFECT_RADIUS);

        List<LivingEntity> affectedEntities = entity.getWorld().getOtherEntities(entity, box)
                .stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .filter(e -> e.distanceTo(entity) <= SHATTER_EFFECT_RADIUS) // Cuz it's a sphere, not a cube
                .toList();

        for (LivingEntity affectedEntity : affectedEntities) {
            addGlassCutStack(affectedEntity, 1800, swordLevel);
        }

        entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, PitchUtils.get());
        sendShatterParticlesPacket(entity);
    }

    public static boolean shouldApplyGlassCut(LivingEntity attacker, int swordLevel, State swordState) {
        double chance = 0;

        if (swordLevel >= 2) {
            chance = 0.3;
        }

        if (swordLevel >= 4) {
            chance += 0.5 * (1 - attacker.getHealth() / attacker.getMaxHealth());

            if (swordState == State.BROKEN) {
                chance = 1.0;
            }
        }

        return MathUtils.takeChance(chance);
    }

    public static void addGlassCutStack(LivingEntity target, int duration, int swordLevel) {
        StatusEffectInstance existingGlassCut = target.getStatusEffect(ModEffects.GLASS_CUT);
        int amplifier = 0;

        if (existingGlassCut != null) {
            int maxAmplifier = (swordLevel >= 5) ? 2 : 0;
            amplifier = Math.min(existingGlassCut.getAmplifier() + 1, maxAmplifier);
            duration = Math.max(existingGlassCut.getDuration(), duration);

            target.removeStatusEffect(ModEffects.GLASS_CUT);
        }

        target.addStatusEffect(new StatusEffectInstance(ModEffects.GLASS_CUT, duration, amplifier, false, false, true));
    }

    public static State getGlassState(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof GlassSwordItem)) {
            SatisfyingWeapons.LOGGER.warn("Trying to read GlassState from an item which is not GlassSword");
        }

        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        int stateId = stackNbt.getInt(GLASS_STATE_NBT_KEY); // if doesn't exist, returns 0 (INTACT) by default

        return State.values()[stateId];
    }

    public static void setGlassState(ItemStack itemStack, State state) {
        if (!(itemStack.getItem() instanceof GlassSwordItem)) {
            SatisfyingWeapons.LOGGER.warn("Trying to set GlassState for an item which is not GlassSword");
            return;
        }

        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        stackNbt.putInt(GLASS_STATE_NBT_KEY, state.ordinal());
    }

    public static boolean isOfState(ItemStack stack, State state) {
        return stack.getItem() instanceof GlassSwordItem && getGlassState(stack) == state;
    }

    public static boolean isIntactSword(ItemStack stack) {
        return isOfState(stack, State.INTACT);
    }

    public static boolean isCrackedSword(ItemStack stack) {
        return isOfState(stack, State.CRACKED);
    }

    public static boolean isBrokenSword(ItemStack stack) {
        return isOfState(stack, State.BROKEN);
    }

    public static boolean isOfGlassPane(ItemStack stack) {
        return stack.isOf(Items.GLASS_PANE) ||(stack.getItem() instanceof BlockItem blockMaterial && blockMaterial.getBlock() instanceof StainedGlassPaneBlock);
    }

    private static void sendShatterParticlesPacket(LivingEntity attacker) {
        Vector3f particlePos = attacker.getPos().toVector3f();
        particlePos.add(0, attacker.getHeight() * 0.3f, 0);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(particlePos);

        BlockPos blockPos = PosUtils.toBlockPos(attacker.getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)attacker.getWorld(), blockPos)) {
            ServerPlayNetworking.send(player, ModNetworking.GLASS_SHATTER_PARTICLES_ID, buf);
        }
    }

    public enum State {
        INTACT,
        CRACKED,
        BROKEN
    }
}
