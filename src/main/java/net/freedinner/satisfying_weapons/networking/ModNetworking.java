package net.freedinner.satisfying_weapons.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.networking.c2s.FireworkJumpClientPacket;
import net.freedinner.satisfying_weapons.networking.s2c.*;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier WISHING_STAR_PARTICLES_ID = SatisfyingWeapons.id("wishing_star_particles");

    public static final Identifier FESTIVITY_GAINED_PARTICLES_ID = SatisfyingWeapons.id("festivity_gained_particles");
    public static final Identifier FIREWORK_JUMP_PARTICLES_ID = SatisfyingWeapons.id("firework_jump_particles");
    public static final Identifier FIREWORK_TRAIL_PARTICLES_ID = SatisfyingWeapons.id("firework_trail_particles");
    public static final Identifier PLUNGE_ATTACK_PARTICLES_ID = SatisfyingWeapons.id("plunge_attack_particles");

    public static final Identifier GLASS_SHATTER_PARTICLES_ID = SatisfyingWeapons.id("glass_sword_shatter_particles");
    public static final Identifier BLOOD_DRIP_PARTICLES_ID = SatisfyingWeapons.id("blood_drip_particles");
    public static final Identifier GLASS_CUT_DAMAGE_PARTICLES_ID = SatisfyingWeapons.id("glass_cut_damage_particles");

    public static final Identifier CONFETTI_PARTICLES_ID = SatisfyingWeapons.id("confetti_particles");
    public static final Identifier GIFT_SMOKE_PARTICLES_ID = SatisfyingWeapons.id("gift_smoke_particles");
    public static final Identifier GIFT_EXPLOSION_PARTICLES_ID = SatisfyingWeapons.id("gift_explosion_particles");

    public static final Identifier SYNC_USE_TIME_LEFT_ID = SatisfyingWeapons.id("sync_use_time_left");

    public static final Identifier BLACK_HOLE_PULL_PARTICLES_ID = SatisfyingWeapons.id("black_hole_pull_particles");
    public static final Identifier ENTROPY_PARTICLES_ID = SatisfyingWeapons.id("entropy_particles");
    public static final Identifier BLACK_HOLE_EXPLOSION_PARTICLES_ID = SatisfyingWeapons.id("black_hole_explosion_particles");

    public static void registerS2CPackets() {
        SatisfyingWeapons.LOGGER.info("Registering S2C packets");

        ClientPlayNetworking.registerGlobalReceiver(WISHING_STAR_PARTICLES_ID, WishingStarParticlesPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(FESTIVITY_GAINED_PARTICLES_ID, FestivityGainedParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FIREWORK_JUMP_PARTICLES_ID, FireworkJumpParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FIREWORK_TRAIL_PARTICLES_ID, FireworkTrailParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLUNGE_ATTACK_PARTICLES_ID, PlungeAttackParticlesPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(GLASS_SHATTER_PARTICLES_ID, GlassShatterParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLOOD_DRIP_PARTICLES_ID, BloodDripParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(GLASS_CUT_DAMAGE_PARTICLES_ID, GlassCutDamageParticlesPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(CONFETTI_PARTICLES_ID, ConfettiParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(GIFT_SMOKE_PARTICLES_ID, GiftSmokeParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(GIFT_EXPLOSION_PARTICLES_ID, GiftExplosionParticlesPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(SYNC_USE_TIME_LEFT_ID, SyncUseTimeLeftPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(BLACK_HOLE_PULL_PARTICLES_ID, BlackHolePullParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ENTROPY_PARTICLES_ID, EntropyParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BLACK_HOLE_EXPLOSION_PARTICLES_ID, BlackHoleExplosionParticlesPacket::receive);
    }

    public static final Identifier FIREWORK_JUMP_CLIENT_PACKET = SatisfyingWeapons.id("firework_jump_client");

    public static void registerC2SPackets() {
        SatisfyingWeapons.LOGGER.info("Registering C2S packets");

        ServerPlayNetworking.registerGlobalReceiver(FIREWORK_JUMP_CLIENT_PACKET, FireworkJumpClientPacket::receive);
    }
}
