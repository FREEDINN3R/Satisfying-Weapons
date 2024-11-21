package net.freedinner.satisfying_weapons.effect.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.List;

public class BirthdayPartyEffect extends StatusEffect {
    public BirthdayPartyEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    /*@Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.isAlive() || entity.isRemoved()) {
            clearAffectedAttackers(entity);
            return;
        }

        if (entity.age % 10 == 0) {
            Box box = new Box(entity.getBlockPos()).expand(16, 8, 16);
            List<MobEntity> otherEntities = entity.world.getOtherEntities(entity, box)
                    .stream()
                    .filter(e -> e instanceof MobEntity)
                    .map(e -> (MobEntity) e)
                    .toList();

            for (MobEntity otherEntity : otherEntities) {
                otherEntity.setTarget(entity);
            }
        }

        if (entity.isAlive() && !entity.world.isClient) {
            sendConfettiParticlesPacket(entity, entity.getStatusEffect(this).getDuration());
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        clearAffectedAttackers(entity);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private void clearAffectedAttackers(LivingEntity target) {
        Box box = new Box(target.getBlockPos()).expand(32, 16, 32);
        List<MobEntity> otherEntities = target.world.getOtherEntities(target, box)
                .stream()
                .filter(e -> e instanceof MobEntity)
                .map(e -> (MobEntity) e)
                .toList();

        for (MobEntity otherEntity : otherEntities) {
            if (otherEntity.getTarget() == target) {
                otherEntity.setTarget(null);
            }
        }
    }

    private void sendConfettiParticlesPacket(LivingEntity entity, int duration) {
        int roundedRatio = (int) Math.floor(10.0f * (duration - 1) / ModConstants.BIRTHDAY_PARTY_EFFECT_DURATION);
        int count = switch (roundedRatio) {
            case 9 -> 8;
            case 8 -> 5;
            case 7 -> 3;
            case 6, 5 -> 2;
            default -> 1;
        };

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(entity.getPos().add(0, entity.getHeight() / 2, 0).toVector3f());
        buf.writeInt(count);

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.world, entity.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworkingPackets.CONFETTI_PARTICLES_ID, buf);
        }
    }*/
}
