package net.freedinner.satisfying_weapons.event.handler;

import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.CrimsonKatanaSwordItem;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AfterDamageCrimsonKatana implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        Entity attacker = source.getAttacker();

        if (!(attacker instanceof PlayerEntity playerAttacker)) {
            return;
        }

        CrimsonKatanaSwordItem katanaItem = searchHotbarForKatana(playerAttacker);

        if (katanaItem == null) {
            return;
        }

        List<String> possibleEffects = Arrays.asList("wither");
        if (katanaItem.getLevel() >= 2) possibleEffects.add("poison");
        if (katanaItem.getLevel() >= 4) possibleEffects.add("burn");

        String chosenEffect = MathUtils.getRandomElement(possibleEffects);
        switch (chosenEffect) {
            case "wither" -> {
                StatusEffectInstance existingWither = entity.getStatusEffect(StatusEffects.WITHER);
                int oldDuration = (existingWither == null) ? 0 : existingWither.getDuration();
                int newDuration = Math.min(360, 120 + oldDuration);

                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, newDuration, 0));
            }
            case "poison" -> {
                StatusEffectInstance existingPoison = entity.getStatusEffect(StatusEffects.POISON);
                int oldDuration = (existingPoison == null) ? 0 : existingPoison.getDuration();
                int newDuration = Math.min(360, 120 + oldDuration);

                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, newDuration, 0));
            }
            case "burn" -> {
                int oldDuration = entity.getFireTicks();
                int newDuration = Math.min(360, 120 + oldDuration);

                entity.setOnFireFor(newDuration);
            }
        }
    }

    @Nullable
    private static CrimsonKatanaSwordItem searchHotbarForKatana(PlayerEntity playerAttacker) {
        PlayerInventory inventory = playerAttacker.getInventory();
        CrimsonKatanaSwordItem katanaItem = null;

        ItemStack offhandStack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
        if (offhandStack.getItem() instanceof CrimsonKatanaSwordItem foundKatanaItem) {
            katanaItem = foundKatanaItem;
        }

        for (int i = 0; PlayerInventory.isValidHotbarIndex(i) && katanaItem == null; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof CrimsonKatanaSwordItem foundKatanaItem) {
                katanaItem = foundKatanaItem;
            }
        }
        return katanaItem;
    }
}
