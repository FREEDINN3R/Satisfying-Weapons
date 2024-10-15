package net.freedinner.satisfying_weapons.loot;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.loot.custom.WishCooldownDropCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.JsonSerializer;

public class ModLootConditions {
    public static final LootConditionType WISH_COOLDOWN = register("wish_cooldown", new WishCooldownDropCondition.Serializer());

    private static LootConditionType register(String name, JsonSerializer<? extends LootCondition> serializer) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, SatisfyingWeapons.id(name), new LootConditionType(serializer));
    }

    public static void registerLootConditions() {
        SatisfyingWeapons.LOGGER.info("Registering loot conditions");
    }
}
