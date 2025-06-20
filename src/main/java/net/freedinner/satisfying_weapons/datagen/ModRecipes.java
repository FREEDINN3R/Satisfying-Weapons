package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.block.ModBlocks;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRecipes {
    public static void generateRecipes(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating recipes");

        pack.addProvider(ModRecipeGenerator::new);
    }

    private static class ModRecipeGenerator extends FabricRecipeProvider {
        private ModRecipeGenerator(FabricDataOutput generator) {
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

            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.BLOCK_OF_LFOS)
                    .pattern(" U ")
                    .pattern("@#W")
                    .pattern(" & ")
                    .input('U', Items.FLOWER_POT)
                    .input('@', Items.HONEYCOMB)
                    .input('#', Blocks.DIAMOND_BLOCK)
                    .input('W', Items.BOOK)
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
        }
    }
}
