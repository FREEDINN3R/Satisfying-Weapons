package net.freedinner.satisfying_weapons.block;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.block.custom.DeconstructorBlock;
import net.freedinner.satisfying_weapons.block.custom.LofsBlock;
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

import java.util.function.BiFunction;

public class ModBlocks {
    public static final Block UPGRADER = registerBlock("upgrader",
            new UpgraderBlock(AbstractBlock.Settings
                    .create()
                    .mapColor(MapColor.BLUE)
                    .strength(1.5f)
                    .sounds(BlockSoundGroup.WOOD)
            ));
    public static final Block DECONSTRUCTOR = registerBlock("deconstructor",
            new DeconstructorBlock(AbstractBlock.Settings
                    .create()
                    .mapColor(MapColor.RED)
                    .strength(1.5f)
                    .sounds(BlockSoundGroup.WOOD)
            ));
    public static final Block BLOCK_OF_LFOS = registerBlock("block_of_lfos",
            new Block(AbstractBlock.Settings
                    .create()
                    .mapColor(MapColor.DARK_GREEN)
                    .strength(4f)
                    .sounds(BlockSoundGroup.STONE)
            ));

    public static final Block BLOCK_OF_LOFS = registerBlock("block_of_lofs",
            new LofsBlock(AbstractBlock.Settings
                    .create()
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .strength(4f)
                    .sounds(BlockSoundGroup.ROOTED_DIRT)
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