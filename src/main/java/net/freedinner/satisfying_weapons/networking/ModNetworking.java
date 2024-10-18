package net.freedinner.satisfying_weapons.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.networking.c2s.FireworkJumpClientPacket;
import net.freedinner.satisfying_weapons.networking.s2c.FireworkJumpParticlesPacket;
import net.freedinner.satisfying_weapons.networking.s2c.FireworkTrailParticlesPacket;
import net.freedinner.satisfying_weapons.networking.s2c.PlungeAttackParticlesPacket;
import net.freedinner.satisfying_weapons.networking.s2c.WishingStarParticlesPacket;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier WISHING_STAR_PARTICLES_ID = SatisfyingWeapons.id("wishing_star_particles");

    public static final Identifier FIREWORK_JUMP_PARTICLES_ID = SatisfyingWeapons.id("firework_jump_particles");
    public static final Identifier FIREWORK_TRAIL_PARTICLES_ID = SatisfyingWeapons.id("firework_trail_particles");
    public static final Identifier PLUNGE_ATTACK_PARTICLES_ID = SatisfyingWeapons.id("plunge_attack_particles");

    public static void registerS2CPackets() {
        SatisfyingWeapons.LOGGER.info("Registering S2C packets");

        ClientPlayNetworking.registerGlobalReceiver(WISHING_STAR_PARTICLES_ID, WishingStarParticlesPacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(FIREWORK_JUMP_PARTICLES_ID, FireworkJumpParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(FIREWORK_TRAIL_PARTICLES_ID, FireworkTrailParticlesPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PLUNGE_ATTACK_PARTICLES_ID, PlungeAttackParticlesPacket::receive);
    }

    public static final Identifier FIREWORK_JUMP_CLIENT_PACKET = SatisfyingWeapons.id("firework_jump_client");

    public static void registerC2SPackets() {
        SatisfyingWeapons.LOGGER.info("Registering C2S packets");

        ServerPlayNetworking.registerGlobalReceiver(FIREWORK_JUMP_CLIENT_PACKET, FireworkJumpClientPacket::receive);
    }
}
