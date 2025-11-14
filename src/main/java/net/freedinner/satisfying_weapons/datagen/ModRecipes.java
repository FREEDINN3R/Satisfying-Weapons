package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.block.ModBlocks;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;

public class ModRecipes {
    public static void generateRecipes(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating recipes");

        pack.addProvider(ModRecipeProvider::new);
    }

    private static class ModRecipeProvider extends FabricRecipeProvider {
        private ModRecipeProvider(FabricDataOutput generator) {
            super(generator);
        }

        @Override
        public void generate(Consumer<RecipeJsonProvider> exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.WISHING_STAR)
                    .pattern(" * ")
                    .pattern("***")
                    .pattern(" * ")
                    .input('*', ModItems.UNFULFILLED_WISH)
                    .criterion(FabricRecipeProvider.hasItem(ModItems.UNFULFILLED_WISH),
                            FabricRecipeProvider.conditionsFromItem(ModItems.UNFULFILLED_WISH))
                    .offerTo(exporter);

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.UPGRADER)
                    .pattern("*")
                    .pattern("#")
                    .input('*', ModItems.UNFULFILLED_WISH)
                    .input('#', Blocks.CRAFTING_TABLE)
                    .criterion(FabricRecipeProvider.hasItem(ModItems.UNFULFILLED_WISH),
                            FabricRecipeProvider.conditionsFromItem(ModItems.UNFULFILLED_WISH))
                    .criterion(FabricRecipeProvider.hasItem(Blocks.CRAFTING_TABLE),
                            FabricRecipeProvider.conditionsFromItem(Blocks.CRAFTING_TABLE))
                    .offerTo(exporter);

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DECONSTRUCTOR)
                    .pattern("*")
                    .pattern("#")
                    .input('*', ModItems.UNFULFILLED_WISH)
                    .input('#', Blocks.FURNACE)
                    .criterion(FabricRecipeProvider.hasItem(ModItems.UNFULFILLED_WISH),
                            FabricRecipeProvider.conditionsFromItem(ModItems.UNFULFILLED_WISH))
                    .criterion(FabricRecipeProvider.hasItem(Blocks.FURNACE),
                            FabricRecipeProvider.conditionsFromItem(Blocks.FURNACE))
                    .offerTo(exporter);

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.BLOCK_OF_LFOS)
                    .pattern(" U ")
                    .pattern("@#P")
                    .pattern(" & ")
                    .input('U', Items.FLOWER_POT)
                    .input('@', Items.HONEYCOMB)
                    .input('#', Blocks.NETHERITE_BLOCK)
                    .input('P', Blocks.QUARTZ_PILLAR)
                    .input('&', Items.POTION)
                    .criterion(FabricRecipeProvider.hasItem(Items.FLOWER_POT),
                            FabricRecipeProvider.conditionsFromItem(Items.FLOWER_POT))
                    .criterion(FabricRecipeProvider.hasItem(Items.HONEYCOMB),
                            FabricRecipeProvider.conditionsFromItem(Items.HONEYCOMB))
                    .criterion(FabricRecipeProvider.hasItem(Blocks.DIAMOND_BLOCK),
                            FabricRecipeProvider.conditionsFromItem(Blocks.DIAMOND_BLOCK))
                    .criterion(FabricRecipeProvider.hasItem(Items.BOOK),
                            FabricRecipeProvider.conditionsFromItem(Items.BOOK))
                    .criterion(FabricRecipeProvider.hasItem(Items.POTION),
                            FabricRecipeProvider.conditionsFromItem(Items.POTION))
                    .offerTo(exporter);

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.BLOCK_OF_LOFS)
                    .pattern(" o ")
                    .pattern("@#P")
                    .pattern(" & ")
                    .input('o', Items.SLIME_BALL)
                    .input('@', Items.HONEYCOMB)
                    .input('#', Blocks.BEDROCK)
                    .input('P', Items.PAPER)
                    .input('&', Items.POTION)
                    .criterion(FabricRecipeProvider.hasItem(Items.SLIME_BALL),
                            FabricRecipeProvider.conditionsFromItem(Items.SLIME_BALL))
                    .criterion(FabricRecipeProvider.hasItem(Items.HONEYCOMB),
                            FabricRecipeProvider.conditionsFromItem(Items.HONEYCOMB))
                    .criterion(FabricRecipeProvider.hasItem(Blocks.BEDROCK),
                            FabricRecipeProvider.conditionsFromItem(Blocks.BEDROCK))
                    .criterion(FabricRecipeProvider.hasItem(Items.PAPER),
                            FabricRecipeProvider.conditionsFromItem(Items.PAPER))
                    .criterion(FabricRecipeProvider.hasItem(Items.POTION),
                            FabricRecipeProvider.conditionsFromItem(Items.POTION))
                    .offerTo(exporter);
        }
    }
}
