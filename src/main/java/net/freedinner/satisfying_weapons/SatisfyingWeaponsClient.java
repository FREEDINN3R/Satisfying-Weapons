package net.freedinner.satisfying_weapons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.freedinner.satisfying_weapons.item.ModItemPredicates;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SatisfyingWeaponsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModItemPredicates.registerItemPredicates();
	}
}