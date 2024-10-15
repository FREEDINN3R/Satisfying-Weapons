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
        PlayerEntity player = lootContext.get(LootContextParameters.LAST_DAMAGE_PLAYER);

        if (player == null) {
            SatisfyingWeapons.LOGGER.warn("An entity was killed by the player, but LAST_DAMAGE_PLAYER was empty");
            return false;
        }

        long lastDropTime = ((IPlayerDataSaver) player).getLastDropTime();
        long currTime = player.getWorld().getTime();
        long timePassed = currTime - lastDropTime;

        if (timePassed < 600) {
            return false;
        }

        float chance = 0.1f + Math.min(timePassed - 600, 1200) / 12000f;
        boolean b = MathUtils.takeChance(chance, player.getWorld());

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
