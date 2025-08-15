package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModTags {
    public static final TagKey<Item> MOD_WEAPONS = TagKey.of(RegistryKeys.ITEM, SatisfyingWeapons.id("mod_weapons"));
    public static final TagKey<Item> MOD_BOWS = TagKey.of(RegistryKeys.ITEM, SatisfyingWeapons.id("mod_bows"));

    public static void registerTags() {
        SatisfyingWeapons.LOGGER.info("Registering tags");
    }

    public static void generateTags(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating tags");

        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModDamageTypeTagProvider::new);
    }

    private static class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
        public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            FabricTagBuilder builder = this.getOrCreateTagBuilder(MOD_WEAPONS);
            addAll(ModItems.FIREWORK_SWORD, builder);
            addAll(ModItems.TOY_BOW, builder);
            addAll(ModItems.GLASS_SWORD, builder);
            addAll(ModItems.SWORD_OF_DYING_STAR, builder);

            builder = this.getOrCreateTagBuilder(MOD_BOWS);
            addAll(ModItems.TOY_BOW, builder);
        }

        private static void addAll(List<Item> items, FabricTagBuilder builder) {
            for (Item item : items) {
                builder.add(item);
            }
        }
    }

    public static class ModDamageTypeTagProvider extends FabricTagProvider<DamageType> {
        public ModDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            addToAllTags(ModDamageTypes.GLASS_CUT,
                    DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_ARMOR,
                    DamageTypeTags.BYPASSES_SHIELD,
                    DamageTypeTags.NO_IMPACT,
                    DamageTypeTags.NO_ANGER
            );
        }

        private void addToAllTags(RegistryKey<DamageType> damageType, TagKey<DamageType> ... tags) {
            for(TagKey<DamageType> tag : tags) {
                getOrCreateTagBuilder(tag).addOptional(damageType);
            }
        }
    }
}
