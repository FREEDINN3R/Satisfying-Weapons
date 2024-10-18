package net.freedinner.satisfying_weapons.effect.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.custom.FireworkSwordItem;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FireworkJumpEffect extends StatusEffect {
    public FireworkJumpEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) {
            return;
        }

        if (entity.isOnGround() || entity.isTouchingWater() || !FireworkSwordItem.heldInHand(entity)
                || entity.isFallFlying() || entity.hasVehicle()
                || entity.hasStatusEffect(StatusEffects.LEVITATION) || entity.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            entity.removeStatusEffect(this);
        }

        if (entity.isSneaking() && entity.getVelocity().y > -5) {
            entity.addVelocity(0, -0.15, 0);
            entity.velocityModified = true;
        }

        if (entity.getVelocity().y > 0 || (entity.isSneaking() && entity.getVelocity().y < 0)) {
            sendTrailPacket(entity, entity.getVelocity().y);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        if (entity.isOnGround() && entity.isSneaking() && FireworkSwordItem.heldInHand(entity)) {
            entity.damage(entity.getWorld().getDamageSources().fall(), 0.01f);

            Box box = new Box(entity.getBlockPos()).expand(2.5, 1, 2.5);
            List<LivingEntity> surroundingEntities = entity.getWorld().getOtherEntities(entity, box)
                    .stream()
                    .filter(e -> e instanceof LivingEntity)
                    .map(e -> (LivingEntity) e)
                    .toList();

            FireworkSwordItem fireworkSword = (FireworkSwordItem) entity.getStackInHand(Hand.MAIN_HAND).getItem();
            float plungeDamage = 2 * fireworkSword.getAttackDamage() * (amplifier + 1);
            DamageSource damageSource = (entity instanceof PlayerEntity player) ?
                    player.getDamageSources().playerAttack(player) :
                    entity.getDamageSources().mobAttack(entity);

            for (LivingEntity otherEntity : surroundingEntities) {
                otherEntity.damage(damageSource, plungeDamage);

                Vec3d direction = entity.getPos().subtract(otherEntity.getPos()).normalize();
                otherEntity.takeKnockback(0.8, direction.x, direction.z);
                otherEntity.velocityModified = true;
            }

            int entitiesHit = surroundingEntities.size();
            FestivityEffect.addStacks(entity, entitiesHit, 10);

            entity.heal(2);

            entity.getWorld().playSound(null, entity.getBlockPos(), ModSounds.PLUNGE_ATTACK, SoundCategory.PLAYERS, 2.0f, PitchUtils.get());
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 1.0f);
            sendPlungeParticlesPacket(entity);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private void sendTrailPacket(LivingEntity entity, double yv) {
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
