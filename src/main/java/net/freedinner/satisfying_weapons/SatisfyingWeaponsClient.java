package net.freedinner.satisfying_weapons;

import net.fabricmc.api.ClientModInitializer;
import net.freedinner.satisfying_weapons.item.ModItemPredicates;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.particle.ModParticles;

public class SatisfyingWeaponsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModItemPredicates.registerItemPredicates();

		ModParticles.registerParticlesClient();

		ModNetworking.registerS2CPackets();
	}
}