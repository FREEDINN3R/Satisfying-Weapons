package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModAdvancements {
    public static Advancement ROOT_ADVANCEMENT;
    public static Advancement GLIMMER_OF_HOPE;
    public static Advancement GOTTA_COLLECT_EM_ALL;

    public static void generateAdvancements(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating advancements");

        pack.addProvider(AdvancementsProvider::new);
    }

    static class AdvancementsProvider extends FabricAdvancementProvider {
        protected AdvancementsProvider(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> consumer) {
            ROOT_ADVANCEMENT = Advancement.Builder.create()
                    .display(
                            ModItems.WISHING_STAR,
                            Text.translatable("advancement.satisfying_weapons.root.title"),
                            Text.translatable("advancement.satisfying_weapons.root.description"),
                            SatisfyingWeapons.id("textures/gui/advancements/background.png"),
                            AdvancementFrame.TASK,
                            false,
                            false,
                            false
                    )
                    .criterion("killed_any_entity", OnKilledCriterion.Conditions.createPlayerKilledEntity())
                    .build(consumer, SatisfyingWeapons.MOD_ID + "/root");

            GLIMMER_OF_HOPE = Advancement.Builder.create().parent(ROOT_ADVANCEMENT)
                    .display(
                            ModItems.UNFULFILLED_WISH,
                            Text.translatable("advancement.satisfying_weapons.glimmer_of_hope.title"),
                            Text.translatable("advancement.satisfying_weapons.glimmer_of_hope.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("got_unfulfilled_wish", InventoryChangedCriterion.Conditions.items(ModItems.UNFULFILLED_WISH))
                    .build(consumer, SatisfyingWeapons.MOD_ID + "/glimmer_of_hope");

            ItemPredicate anyModWeapon = ItemPredicate.Builder.create().tag(ModTags.MOD_WEAPONS).build();

            GOTTA_COLLECT_EM_ALL = Advancement.Builder.create().parent(ROOT_ADVANCEMENT)
                    .display(
                            ModItems.FIREWORK_SWORD,
                            Text.translatable("advancement.satisfying_weapons.gotta_collect_em_all.title"),
                            Text.translatable("advancement.satisfying_weapons.gotta_collect_em_all.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("rolled_weapon", InventoryChangedCriterion.Conditions.items(anyModWeapon))
                    .build(consumer, SatisfyingWeapons.MOD_ID + "/gotta_collect_em_all");
        }
    }
}
