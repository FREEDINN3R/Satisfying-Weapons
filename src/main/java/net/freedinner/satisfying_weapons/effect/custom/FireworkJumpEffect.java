package net.freedinner.satisfying_weapons.effect.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.custom.FireworkSwordItem;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.freedinner.satisfying_weapons.util.IPlayerDataSaver;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;
import java.util.UUID;

public class FireworkJumpEffect extends StatusEffect {
    private static final int MAX_ON_GROUND_TIME = 1;
    private static final Multimap<EntityAttribute, EntityAttributeModifier> knockbackModifier;

    static {
        // Multimap with a 1.0 value increase for knockback resistance
        knockbackModifier = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
                .put(
                        EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                        new EntityAttributeModifier(
                                UUID.fromString("bc8a023f-1eff-418b-a6c8-a793940feeed"),
                                "firework_jump_knockback_modifier",
                                1.0,
                                EntityAttributeModifier.Operation.ADDITION
                        )
                )
                .build();
    }

    public FireworkJumpEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Launch the player into the air
        Vec3d v = player.getVelocity();
        player.setVelocity(v.x, 1.5, v.z);
        player.velocityModified = true;

        // Add damage and knockback resistance
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(player);
        defenseData.setScale(defenseData.getScale() * 10f);
        attributes.addTemporaryModifiers(knockbackModifier);

        // Reset onGroundTime counter
        setOnGroundTime(player, 0);

        // Visuals & SFX
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 3.0f, 1.0f);
        sendJumpParticlesPacket(player);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) {
            return;
        }

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Update onGroundTime counter
        if (player.isOnGround()) {
            setOnGroundTime(player, getOnGroundTime(player) + 1);
        }
        else {
            setOnGroundTime(player, 0);
        }

        // Remove this effect if something has interrupted the Firework Jump
        if (!isEligible(player, false)) {
            player.removeStatusEffect(this);
            return;
        }

        // Accelerate down when sneaking
        if (player.isSneaking() && player.getVelocity().y > -5) {
            player.addVelocity(0, -0.15, 0);
            player.velocityModified = true;
        }

        // Visuals & SFX
        if (player.getVelocity().y > 0 || (player.isSneaking() && player.getVelocity().y < 0)) {
            sendTrailParticlesPacket(player, player.getVelocity().y);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Remove damage and knockback resistance
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(player);
        defenseData.setScale(defenseData.getScale() / 10f);
        attributes.removeModifiers(knockbackModifier);

        // If plunge attack was correctly performed
        if (getOnGroundTime(player) > MAX_ON_GROUND_TIME && player.isSneaking() && FireworkSwordItem.heldInHand(player)) {
            // Double-check that it was a player

            // Search for surrounding LivingEntities
            Box box = new Box(player.getBlockPos()).expand(2.5, 1, 2.5);
            List<LivingEntity> surroundingEntities = player.getWorld().getOtherEntities(player, box)
                    .stream()
                    .filter(e -> e instanceof LivingEntity)
                    .map(e -> (LivingEntity) e)
                    .toList();

            // For every entity hit
            for (LivingEntity otherEntity : surroundingEntities) {
                // Calculate and apply damage
                float damageMultiplier = 1.5f * (amplifier + 1);
                CombatHelper.simulatePlayerAttack(player, otherEntity, damageMultiplier);

                // Calculate and apply knockback
                Vec3d direction = player.getPos().subtract(otherEntity.getPos()).normalize();
                int knockbackLevel = EnchantmentHelper.getKnockback(player);
                otherEntity.takeKnockback(0.8 + 0.4 * knockbackLevel, direction.x, direction.z);
                otherEntity.velocityModified = true;
            }

            // Add Festivity stacks
            int entitiesHit = surroundingEntities.size();
            FestivityEffect.addStacks(player, entitiesHit, 10);

            // Restore 1 heart
            player.heal(2);

            // Visuals & SFX
            player.getWorld().playSound(null, player.getBlockPos(), ModSounds.PLUNGE_ATTACK, SoundCategory.PLAYERS, 2.0f, PitchUtils.get());
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 1.0f);
            sendPlungeParticlesPacket(player);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static boolean isEligible(LivingEntity entity, boolean checkStartingConditions) {
        // Only players can do a Firework Jump
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        // Check if the player has elytra
        ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
        boolean hasElytra = itemStack.getItem() instanceof ElytraItem && ElytraItem.isUsable(itemStack);

        // Conditions that apply when the player starts a Firework Jump
        boolean startingConditions =
                !player.getAbilities().flying && !hasElytra
                && player.getVelocity().y < 0 && FestivityEffect.getStacks(player) >= 3;

        // Conditions that apply when the player is already doing a Firework Jump
        return (!checkStartingConditions || startingConditions)
                && FireworkSwordItem.heldInHand(player)
                && getOnGroundTime(player) <= MAX_ON_GROUND_TIME && !player.isClimbing() && !player.isFallFlying()
                && !entity.isTouchingWater() && !entity.isInLava() && !entity.hasVehicle()
                && !entity.hasStatusEffect(StatusEffects.LEVITATION) && !entity.hasStatusEffect(StatusEffects.SLOW_FALLING);
    }

    private static int getOnGroundTime(PlayerEntity player) {
        return ((IPlayerDataSaver) player).satisfyingWeapons$getOnGroundTimeFS();
    }

    private static void setOnGroundTime(PlayerEntity player, int time) {
        ((IPlayerDataSaver) player).satisfyingWeapons$setOnGroundTimeFS(time);
    }

    private static void sendJumpParticlesPacket(LivingEntity entity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(entity.getPos().toVector3f());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.FIREWORK_JUMP_PARTICLES_ID, buf);
        }
    }

    private void sendTrailParticlesPacket(LivingEntity entity, double yv) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(entity.getPos().toVector3f());
        buf.writeDouble(yv);

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.FIREWORK_TRAIL_PARTICLES_ID, buf);
        }
    }

    private void sendPlungeParticlesPacket(LivingEntity entity) {
        BlockPos particlesPos = entity.getBlockPos();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(entity.getPos().toVector3f());
        buf.writeBlockPos(particlesPos.down());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.getWorld(), particlesPos)) {
            ServerPlayNetworking.send(player, ModNetworking.PLUNGE_ATTACK_PARTICLES_ID, buf);
        }
    }
}
