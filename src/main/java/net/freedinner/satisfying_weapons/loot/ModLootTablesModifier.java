package net.freedinner.satisfying_weapons.loot;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.loot.custom.WishCooldownDropCondition;
import net.freedinner.satisfying_weapons.mixin.LootTableBuilderAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModLootTablesModifier {
    public static void modifyLootTables() {
        SatisfyingWeapons.LOGGER.info("Modifying loot tables");

        // Add Unfulfilled Wish drop to all hostile entities
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            LootContextType lootType = ((LootTableBuilderAccessor) tableBuilder).getType();

            // If not monster entity, ignore
            if (lootType != LootContextTypes.ENTITY || !isMonsterLoot(id)) {
                return;
            }

            // Building a new pool with conditions
            LootPool.Builder poolBuilder = LootPool.builder()
                    .conditionally(KilledByPlayerLootCondition.builder())
                    .conditionally(WishCooldownDropCondition.builder())
                    .with(ItemEntry.builder(ModItems.UNFULFILLED_WISH));

            tableBuilder.pool(poolBuilder);
        });
    }

    public static boolean isMonsterLoot(Identifier lootTableId) {
        // Get all entities with this loot table
        List<EntityType<?>> matchingEntities = reverseSearchEntities(lootTableId);

        // If every entity in list is a monster
        for (EntityType<?> entity : matchingEntities) {
            if (entity.getSpawnGroup() != SpawnGroup.MONSTER) {
                return false;
            }
        }

        // Safeguard against an empty list
        return !matchingEntities.isEmpty();
    }

    public static List<EntityType<?>> reverseSearchEntities(Identifier lootTableId) {
        // Get all entities with this loot table
        List<EntityType<?>> filteredEntities = Registries.ENTITY_TYPE
                .stream()
                .filter(entityType -> entityType.getLootTableId().equals(lootTableId))
                .toList();

        // If there are multiple entities, send a warning
        if (filteredEntities.size() > 1) {
            SatisfyingWeapons.LOGGER.warn("Found several entities with " + lootTableId + " loot table:");
            for (EntityType<?> entity : filteredEntities) {
                SatisfyingWeapons.LOGGER.warn(Registries.ENTITY_TYPE.getId(entity).toString());
            }
        }

        return filteredEntities;
    }
}
