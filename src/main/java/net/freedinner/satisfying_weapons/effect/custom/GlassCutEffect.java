package net.freedinner.satisfying_weapons.effect.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.freedinner.satisfying_weapons.util.SoundUtils;
import net.freedinner.satisfying_weapons.util.data.ILivingEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
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
            sendBloodDripParticlesPacket(entity);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static void applyDamageTo(LivingEntity target) {
        if (target.getWorld().isClient() || !target.isAlive() || target.isRemoved()) {
            return;
        }

        // Prevents players revived by totems from immediately dying again
        // Technically I could leave it, but that would be too OP
        if (!target.hasStatusEffect(ModEffects.GLASS_CUT)) {
            return;
        }

        // Inherits the attacker if the original instance of damage had one
        LivingEntity lastAttacker = null;
        if (target.getLastAttacker() != null && target.age - target.getLastAttackedTime() <= DAMAGE_DELAY_TICKS + 1) {
            lastAttacker = target.getLastAttacker();
        }

        DamageSource glassCutDamageSource = CombatHelper.getDamageSource(ModDamageTypes.GLASS_CUT, target.getWorld(), lastAttacker);

        // Damage = 2 * lvl
        int amplifier = target.getStatusEffect(ModEffects.GLASS_CUT).getAmplifier();
        target.damage(glassCutDamageSource, 2 * (amplifier + 1));

        target.getWorld().playSound(null, target.getBlockPos(), ModSounds.GLASS_CUT_DAMAGE, SoundCategory.MASTER, 1.0f, SoundUtils.getPitch(0.16f));
        sendDamageParticlesPacket(target);
    }

    private static void sendBloodDripParticlesPacket(LivingEntity entity) {
        double posX = entity.getParticleX(0.1);
        double posY = entity.getBodyY(MathUtils.randomNumber(0.4, 0.8));
        double posZ = entity.getParticleZ(0.1);
        Vec3d particlePos = new Vec3d(posX, posY, posZ);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(particlePos.toVector3f());
        buf.writeVector3f(entity.getPos().toVector3f());

        for (ServerPlayerEntity player : PosUtils.getPlayersTracking(entity)) {
            ServerPlayNetworking.send(player, ModNetworking.BLOOD_DRIP_PARTICLES_ID, buf);
        }
    }

    private static void sendDamageParticlesPacket(LivingEntity target) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(target.getPos().add(0, target.getHeight() * MathUtils.randomNumber(0.3, 0.8), 0).toVector3f());

        for (ServerPlayerEntity player : PosUtils.getPlayersTracking(target)) {
            PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
            bufCopy.writeBoolean(!player.equals(target)); // Don't produce slash for the viewer

            ServerPlayNetworking.send(player, ModNetworking.GLASS_CUT_DAMAGE_PARTICLES_ID, bufCopy);
        }
    }
}
