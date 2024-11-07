package net.freedinner.satisfying_weapons.effect.custom;

import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.item.custom.FireworkSword;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;

public class FestivityEffect extends StatusEffect {
    public FestivityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Clear all stacks if sword is not in hand
        if (!FireworkSword.heldInHand(entity)) {
            entity.removeStatusEffect(this);
            return;
        }

        // If more than 10 stacks, reset stacks
        if (getStacks(entity) > getMaxStacks(entity)) {
            resetStacks(entity);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static int getStacks(LivingEntity entity) {
        if (entity.hasStatusEffect(ModEffects.FESTIVITY)) {
            return entity.getStatusEffect(ModEffects.FESTIVITY).getAmplifier() + 1;
        }

        return 0;
    }

    public static int getMaxStacks(LivingEntity entity) {
        return switch (FireworkSword.getLevel(entity)) {
            case 1, 2, 3, 4 -> 5;
            case 5 -> 10;
            default -> 0;
        };
    }

    public static void resetStacks(LivingEntity entity) {
        addStacks(entity, 0);
    }

    public static boolean addStacks(LivingEntity entity, int amount) {
        int oldStacks = getStacks(entity);
        int newStacks = MathHelper.clamp(oldStacks + amount, 0, getMaxStacks(entity));

        entity.removeStatusEffect(ModEffects.FESTIVITY);
        if (newStacks > 0) {
            entity.addStatusEffect(new StatusEffectInstance(ModEffects.FESTIVITY, -1, newStacks - 1, false, false));
        }

        return newStacks != oldStacks;
    }
}
