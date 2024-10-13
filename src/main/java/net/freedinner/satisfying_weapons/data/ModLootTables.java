package net.freedinner.satisfying_weapons.data;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.mixin.LootTableBuilderAccessor;
import net.freedinner.satisfying_weapons.util.LootTableSearcher;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;

public class ModLootTables {
    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            LootContextType lootType = ((LootTableBuilderAccessor) tableBuilder).getType();

            if (lootType != LootContextTypes.ENTITY || !LootTableSearcher.isMonsterLoot(id)) {
                return;
            }

            LootPool.Builder poolBuilder = LootPool.builder()
                    .conditionally(KilledByPlayerLootCondition.builder())
                    .conditionally(RandomChanceLootCondition.builder(0.1f))
                    .with(ItemEntry.builder(ModItems.UNFULFILLED_WISH));

            tableBuilder.pool(poolBuilder);
        });
    }
}
