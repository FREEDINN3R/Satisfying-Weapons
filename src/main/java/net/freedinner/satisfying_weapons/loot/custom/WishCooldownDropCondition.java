package net.freedinner.satisfying_weapons.loot.custom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.loot.ModLootConditions;
import net.freedinner.satisfying_weapons.util.IPlayerDataSaver;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;

public class WishCooldownDropCondition implements LootCondition {
    protected WishCooldownDropCondition() {}

    @Override
    public LootConditionType getType() {
        return ModLootConditions.WISH_COOLDOWN;
    }

    @Override
    public boolean test(LootContext lootContext) {
        // Get killer player
        PlayerEntity player = lootContext.get(LootContextParameters.LAST_DAMAGE_PLAYER);

        if (player == null) {
            SatisfyingWeapons.LOGGER.warn("An entity was killed by the player, but LAST_DAMAGE_PLAYER was empty");
            return false;
        }

        // How many ticks passed since Unfulfilled Wish last dropped
        long lastDropTime = ((IPlayerDataSaver) player).getLastDropTime();
        long currTime = player.getWorld().getTime();
        long timePassed = currTime - lastDropTime;

        // If less than 30 seconds, no drop
        if (timePassed < 600) {
            return false;
        }

        // After 30 seconds, chance is 10%, with linear increase to 20% during next 60 seconds
        float chance = 0.1f + Math.min(timePassed - 600, 1200) / 12000f;
        boolean b = MathUtils.takeChance(chance, player.getWorld());

        // If drop happened, save new last drop time
        if (b) {
            ((IPlayerDataSaver) player).setLastDropTime(currTime);
        }

        return b;
    }

    public static LootCondition.Builder builder() {
        return WishCooldownDropCondition::new;
    }

    public static class Serializer implements JsonSerializer<WishCooldownDropCondition> {
        public void toJson(JsonObject jsonObject, WishCooldownDropCondition lootCondition, JsonSerializationContext jsonSerializationContext) {
        }

        public WishCooldownDropCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new WishCooldownDropCondition();
        }
    }
}
