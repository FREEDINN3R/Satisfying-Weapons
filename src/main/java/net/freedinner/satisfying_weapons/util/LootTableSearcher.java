package net.freedinner.satisfying_weapons.util;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

public class LootTableSearcher {
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
