package net.freedinner.satisfying_weapons.effect.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.freedinner.satisfying_weapons.util.ILivingEntityDataSaver;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class GlassCutEffect extends StatusEffect {
    public static final int DAMAGE_DELAY_TICKS = 5;

    public GlassCutEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        ((ILivingEntityDataSaver) entity).sw$setGlassCutCountdown(DAMAGE_DELAY_TICKS);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Once per 4 ticks spawn a particle
        if (!entity.getWorld().isClient && MathUtils.takeChance(0.25)) {
            this.sendBloodParticlesPacket(entity);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static void applyDamageTo(LivingEntity target) {
        // Prevents players revived by totems from immediately dying again
        // Technically I could leave it, but that would be too OP
        if (target.getWorld().isClient() || !target.hasStatusEffect(ModEffects.GLASS_CUT)) {
            return;
        }

        // Inherits the attacker if the original instance of damage had one
        LivingEntity lastAttacker = null;
        if (target.getLastAttacker() != null && target.age - target.getLastAttackedTime() <= DAMAGE_DELAY_TICKS + 1) {
            lastAttacker = target.getLastAttacker();
        }

        DamageSource glassCutDamageSource = CombatHelper.getDamageSource(ModDamageTypes.GLASS_CUT, target.getWorld(), lastAttacker);

        // Damage = 2 * lvl
        float amplifier = target.getStatusEffect(ModEffects.GLASS_CUT).getAmplifier();
        target.damage(glassCutDamageSource, 2 * (amplifier + 1));
    }

    private void sendBloodParticlesPacket(LivingEntity entity) {
        double posX = entity.getParticleX(0.1);
        double posY = entity.getBodyY(0.4 + MathUtils.randomNumber(0.4));
        double posZ = entity.getParticleZ(0.1);
        Vec3d pos = new Vec3d(posX, posY, posZ);

        Vec3d v = pos.subtract(entity.getPos());
        v = v.multiply(1, 0, 1).normalize();
        v = v.multiply(MathUtils.randomNumber(0.05, 0.08));

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(pos.toVector3f());
        buf.writeVector3f(v.toVector3f());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)entity.getWorld(), entity.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.BLOOD_DRIP_PARTICLES_ID, buf);
        }
    }
}
