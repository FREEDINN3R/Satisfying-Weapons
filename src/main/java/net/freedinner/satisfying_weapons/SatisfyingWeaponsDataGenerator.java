package net.freedinner.satisfying_weapons;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.freedinner.satisfying_weapons.datagen.ModAdvancements;
import net.freedinner.satisfying_weapons.datagen.ModLoot;
import net.freedinner.satisfying_weapons.datagen.ModRecipes;
import net.freedinner.satisfying_weapons.datagen.ModTags;

public class SatisfyingWeaponsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		ModTags.generateTags(pack);
		ModLoot.generateLoot(pack);
		ModRecipes.generateRecipes(pack);
		ModAdvancements.generateAdvancements(pack);
	}
}
