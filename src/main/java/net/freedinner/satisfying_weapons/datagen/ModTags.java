package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModTags {
    // General tags
    public static final TagKey<Item> ALL_MOD_WEAPONS = registerItemTag("all_mod_weapons");
    public static final TagKey<Item> MOD_BOWS = registerItemTag("mod_bows");

    // Weapon rarity tags
    public static final TagKey<Item> RARE_DROPS = registerItemTag("rare_drops");
    public static final TagKey<Item> EPIC_DROPS = registerItemTag("epic_drops");
    public static final TagKey<Item> LEGENDARY_DROPS = registerItemTag("legendary_drops");

    public static void registerTags() {
        SatisfyingWeapons.LOGGER.info("Registering tags");
    }

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.of(RegistryKeys.ITEM, SatisfyingWeapons.id(name));
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
            addItemsToTag(this.getOrCreateTagBuilder(ALL_MOD_WEAPONS),
                    ModItems.FIREWORK_SWORD,
                    ModItems.GLASS_SWORD,
                    ModItems.CRIMSON_KATANA,
                    ModItems.TOY_BOW,
                    ModItems.MECHANICAL_SWORD,
                    ModItems.SWORD_OF_DYING_STAR);

            addItemsToTag(this.getOrCreateTagBuilder(MOD_BOWS),
                    ModItems.TOY_BOW);

            addItemsToTag(this.getOrCreateTagBuilder(RARE_DROPS),
                    ModItems.FIREWORK_SWORD,
                    ModItems.GLASS_SWORD,
                    ModItems.CRIMSON_KATANA);

            addItemsToTag(this.getOrCreateTagBuilder(EPIC_DROPS),
                    ModItems.TOY_BOW,
                    ModItems.MECHANICAL_SWORD);

            addItemsToTag(this.getOrCreateTagBuilder(LEGENDARY_DROPS),
                    ModItems.SWORD_OF_DYING_STAR);
        }

        private static void addItemsToTag(FabricTagBuilder tag, Item... items) {
            for (Item item : items) {
                tag.add(item);
            }
        }

        @SafeVarargs
        private static void addItemsToTag(FabricTagBuilder tag, List<Item>... items) {
            for (List<Item> itemList : items) {
                for (Item item : itemList) {
                    tag.add(item);
                }
            }
        }
    }

    public static class ModDamageTypeTagProvider extends FabricTagProvider<DamageType> {
        public ModDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            addDamageToTags(ModDamageTypes.GLASS_CUT,
                    DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_ARMOR,
                    DamageTypeTags.NO_IMPACT,
                    DamageTypeTags.NO_ANGER
            );

            addDamageToTags(ModDamageTypes.INSTANT_WITHER,
                    DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_ARMOR
            );

            addDamageToTags(ModDamageTypes.INSTANT_POISON,
                    DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_ARMOR,
                    DamageTypeTags.WITCH_RESISTANT_TO,
                    DamageTypeTags.AVOIDS_GUARDIAN_THORNS,
                    DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH
            );

            addDamageToTags(ModDamageTypes.INSTANT_BURN,
                    DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.BYPASSES_ARMOR,
                    DamageTypeTags.IS_FIRE,
                    DamageTypeTags.BURNS_ARMOR_STANDS
            );
        }

        @SafeVarargs
        private void addDamageToTags(RegistryKey<DamageType> damageType, TagKey<DamageType> ... tags) {
            for(TagKey<DamageType> tag : tags) {
                getOrCreateTagBuilder(tag).addOptional(damageType);
            }
        }
    }
}
