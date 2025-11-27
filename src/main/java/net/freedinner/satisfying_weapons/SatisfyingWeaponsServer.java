package net.freedinner.satisfying_weapons;

import net.fabricmc.api.DedicatedServerModInitializer;

public class SatisfyingWeaponsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		PlayerWishDataManager.loadChestLootTables();
	}
}