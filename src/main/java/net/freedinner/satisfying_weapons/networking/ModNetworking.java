package net.freedinner.satisfying_weapons.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.networking.s2c.WishingStarParticlesPacket;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier WISHING_STAR_PARTICLES_ID = SatisfyingWeapons.id("wishing_star_particles");

    public static void registerS2CPackets() {
        SatisfyingWeapons.LOGGER.info("Registering S2C packets");

        ClientPlayNetworking.registerGlobalReceiver(WISHING_STAR_PARTICLES_ID, WishingStarParticlesPacket::receive);
    }

    public static void registerC2SPackets() {
        SatisfyingWeapons.LOGGER.info("Registering C2S packets");
    }
}
