package net.freedinner.satisfying_weapons;

import net.fabricmc.api.ModInitializer;

import net.freedinner.satisfying_weapons.loot.ModLootConditions;
import net.freedinner.satisfying_weapons.loot.ModLootTablesModifier;
import net.freedinner.satisfying_weapons.item.ModItemGroups;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SatisfyingWeapons implements ModInitializer {
	public static final String MOD_ID = "satisfying_weapons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModItems.registerItems();
		ModItemGroups.registerItemGroups();

		ModLootConditions.registerLootConditions();
		ModLootTablesModifier.modifyLootTables();

		ModNetworking.registerS2CPackets();
	}
}