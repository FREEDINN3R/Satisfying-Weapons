package net.freedinner.satisfying_weapons.effect.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class EntropyEffect extends StatusEffect {
    public EntropyEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        // Modify fall damage and defense
        ScaleData fallDamageData = ScaleTypes.FALLING.getScaleData(entity);
        fallDamageData.setScale(fallDamageData.getScale() * 1.5f * 0.8f); // Not exactly 50%, since it modifies fall distance, but close
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
        defenseData.setScale(defenseData.getScale() * 0.8f);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // About once per 1.5 seconds
        if (!entity.getWorld().isClient && MathUtils.takeChance(0.03)) {
            this.sendParticlesPacket(entity);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        // Reset fall damage and defense
        ScaleData fallDamageData = ScaleTypes.FALLING.getScaleData(entity);
        fallDamageData.setScale(fallDamageData.getScale() / 1.5f / 0.8f);
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
        defenseData.setScale(defenseData.getScale() / 0.8f);

        super.onRemoved(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private void sendParticlesPacket(LivingEntity entity) {
        double posX = entity.getParticleX(0.5);
        double posY = entity.getRandomBodyY();
        double posZ = entity.getParticleZ(0.5);
        Vec3d pos = new Vec3d(posX, posY, posZ);

        Vec3d v = pos.subtract(entity.getPos());
        v = v.multiply(1, 0, 1).normalize();
        v = v.multiply(MathUtils.randomNumber(0.05, 0.12));

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(pos.toVector3f());
        buf.writeVector3f(v.toVector3f());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.ENTROPY_PARTICLES_ID, buf);
        }
    }
}
