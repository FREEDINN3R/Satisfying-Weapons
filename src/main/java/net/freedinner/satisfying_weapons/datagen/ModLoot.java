package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.block.ModBlocks;

public class ModLoot {
    public static void generateLoot(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating loot");

        pack.addProvider(ModBlockLootGenerator::new);
    }

    private static class ModBlockLootGenerator extends FabricBlockLootTableProvider {
        protected ModBlockLootGenerator(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            addDrop(ModBlocks.UPGRADER, drops(ModBlocks.UPGRADER));
        }
    }
}
