package net.freedinner.satisfying_weapons.effect.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.custom.FireworkSwordItem;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
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

        // Launch the player into the air
        Vec3d v = entity.getVelocity();
        entity.setVelocity(v.x, 1.5, v.z);
        entity.velocityModified = true;

        // Add damage and knockback resistance
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
        defenseData.setScale(defenseData.getScale() * 10f);
        attributes.addTemporaryModifiers(knockbackModifier);

        // Visuals & SFX
        entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 3.0f, 1.0f);
        sendJumpParticlesPacket(entity);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) {
            return;
        }

        // Remove effect if something has interrupted the Firework Jump
        if (!isEligible(entity, false)) {
            entity.removeStatusEffect(this);
            return;
        }

        // Accelerate down when sneaking
        if (entity.isSneaking() && entity.getVelocity().y > -5) {
            entity.addVelocity(0, -0.15, 0);
            entity.velocityModified = true;
        }

        // Visuals & SFX
        if (entity.getVelocity().y > 0 || (entity.isSneaking() && entity.getVelocity().y < 0)) {
            sendTrailParticlesPacket(entity, entity.getVelocity().y);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        // Remove damage and knockback resistance
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
        defenseData.setScale(defenseData.getScale() / 10f);
        attributes.removeModifiers(knockbackModifier);

        // If plunge attack was correctly performed
        if (entity.isOnGround() && entity.isSneaking() && FireworkSwordItem.heldInHand(entity)) {
            // Double-check that it was a player
            if (!(entity instanceof PlayerEntity player)) {
                return;
            }

            // Search for surrounding LivingEntities
            Box box = new Box(player.getBlockPos()).expand(2.5, 1, 2.5);
            List<LivingEntity> surroundingEntities = player.getWorld().getOtherEntities(player, box)
                    .stream()
                    .filter(e -> e instanceof LivingEntity)
                    .map(e -> (LivingEntity) e)
                    .toList();

            // Calculate damage
            FireworkSwordItem fireworkSword = (FireworkSwordItem) player.getStackInHand(Hand.MAIN_HAND).getItem();
            float totalDamage = 2 * fireworkSword.getAttackDamage() * (amplifier + 1);
            DamageSource damageSource = player.getDamageSources().playerAttack(player);

            // Damage and apply knockback to entities
            for (LivingEntity otherEntity : surroundingEntities) {
                otherEntity.damage(damageSource, totalDamage);

                Vec3d direction = player.getPos().subtract(otherEntity.getPos()).normalize();
                otherEntity.takeKnockback(0.8, direction.x, direction.z);
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
                && !player.isOnGround() && !player.isClimbing() && !player.isFallFlying()
                && !entity.isTouchingWater() && !entity.isInLava() && !entity.hasVehicle()
                && !entity.hasStatusEffect(StatusEffects.LEVITATION) && !entity.hasStatusEffect(StatusEffects.SLOW_FALLING);
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
