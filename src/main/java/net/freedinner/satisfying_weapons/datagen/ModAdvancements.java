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
    public static final Identifier ROOT_ADVANCEMENT = getAdvancementId("root");
    public static final Identifier GLIMMER_OF_HOPE = getAdvancementId("glimmer_of_hope");
    public static final Identifier GOTTA_COLLECT_EM_ALL = getAdvancementId("gotta_collect_em_all");

    public static void generateAdvancements(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating advancements");

        pack.addProvider(ModAdvancementProvider::new);
    }

    private static Identifier getAdvancementId(String name) {
        return Identifier.of("minecraft", SatisfyingWeapons.MOD_ID + "/" + name);
    }

    static class ModAdvancementProvider extends FabricAdvancementProvider {
        protected ModAdvancementProvider(FabricDataOutput dataGenerator) {
            super(dataGenerator);
        }

        @Override
        public void generateAdvancement(Consumer<Advancement> consumer) {
            Advancement root = Advancement.Builder.create()
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
                    .build(consumer, ROOT_ADVANCEMENT.getPath());

            Advancement.Builder.create().parent(root)
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
                    .build(consumer, GLIMMER_OF_HOPE.getPath());

            ItemPredicate anyModWeapon = ItemPredicate.Builder.create().tag(ModTags.ALL_MOD_WEAPONS).build();

            Advancement.Builder.create().parent(root)
                    .display(
                            ModItems.FIREWORK_SWORD.get(0),
                            Text.translatable("advancement.satisfying_weapons.gotta_collect_em_all.title"),
                            Text.translatable("advancement.satisfying_weapons.gotta_collect_em_all.description"),
                            null,
                            AdvancementFrame.TASK,
                            true,
                            true,
                            false
                    )
                    .criterion("rolled_weapon", InventoryChangedCriterion.Conditions.items(anyModWeapon))
                    .build(consumer, GOTTA_COLLECT_EM_ALL.getPath());
        }
    }
}
