package net.freedinner.satisfying_weapons.block;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.block.custom.UpgraderBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {
    public static final Block UPGRADER = registerBlock("upgrader",
            new UpgraderBlock(AbstractBlock.Settings
                    .create()
                    .mapColor(MapColor.BLUE)
                    .instrument(Instrument.BASS)
                    .strength(2.5f)
                    .sounds(BlockSoundGroup.WOOD)
            ));

    private static Block registerBlock(String name, Block block) {
        Block registeredBlock = Registry.register(Registries.BLOCK, SatisfyingWeapons.id(name), block);
        Registry.register(Registries.ITEM, SatisfyingWeapons.id(name), new BlockItem(block, new Item.Settings()));

        return registeredBlock;
    }

    public static void registerBlocks() {
        SatisfyingWeapons.LOGGER.info("Registering blocks");
    }
}