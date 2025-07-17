package net.freedinner.satisfying_weapons.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
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
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GlassSwordItem extends UpgradeableSwordItem implements FabricItem {
    private static final String GLASS_STATE_NBT_KEY = "glass_state";

    private final Multimap<EntityAttribute, EntityAttributeModifier> brokenAttributeModifiers;

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (getGlassState(stack) == GlassState.BROKEN) {
            return "item.satisfying_weapons.broken_glass_sword";
        }

        return super.getTranslationKey(stack);
    }

    public GlassSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable GlassSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 1, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", 1, EntityAttributeModifier.Operation.ADDITION)
        );
        this.brokenAttributeModifiers = builder.build();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.getDamage() > 1 && getGlassState(stack) != GlassState.BROKEN) {
            setGlassState(stack, GlassState.BROKEN);

            attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, PitchUtils.get());
            this.sendParticlesPacket(attacker);
        }

        if (getGlassState(stack) == GlassState.INTACT) {
            return super.postHit(stack, target, attacker); // for durability and injected behaviors
        }

        return true;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return super.isItemBarVisible(stack) && getGlassState(stack) == GlassState.INTACT;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (getGlassState(stack) == GlassState.BROKEN && slot == EquipmentSlot.MAINHAND) {
            return brokenAttributeModifiers;
        }

        return super.getAttributeModifiers(stack, slot);
    }

    public static GlassState getGlassState(ItemStack itemStack) {
        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        int stateId = stackNbt.getInt(GLASS_STATE_NBT_KEY); // if doesn't exist, returns 0 by default

        return GlassState.values()[stateId];
    }

    public static void setGlassState(ItemStack itemStack, GlassState state) {
        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        stackNbt.putInt(GLASS_STATE_NBT_KEY, state.ordinal());
    }

    public static boolean isBrokenGlass(ItemStack stack) {
        return stack.getItem() instanceof GlassSwordItem && getGlassState(stack) == GlassState.BROKEN;
    }

    public static boolean isGlassPane(ItemStack stack) {
        return stack.isOf(Items.GLASS_PANE) ||(stack.getItem() instanceof BlockItem blockMaterial && blockMaterial.getBlock() instanceof StainedGlassPaneBlock);
    }

    private void sendParticlesPacket(LivingEntity attacker) {
        Vector3f particlePos = attacker.getPos().toVector3f();
        particlePos.add(0, attacker.getHeight() * 0.3f, 0);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(particlePos);

        BlockPos blockPos = PosUtils.toBlockPos(attacker.getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)attacker.getWorld(), blockPos)) {
            ServerPlayNetworking.send(player, ModNetworking.GLASS_SWORD_PARTICLES_ID, buf);
        }
    }

    public enum GlassState {
        INTACT,
        CRACKED,
        BROKEN
    }
}
