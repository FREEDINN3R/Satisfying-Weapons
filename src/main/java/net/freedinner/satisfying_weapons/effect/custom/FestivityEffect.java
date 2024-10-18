package net.freedinner.satisfying_weapons.effect.custom;

import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.item.custom.FireworkSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class FestivityEffect extends StatusEffect {
    public FestivityEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!FireworkSwordItem.heldInHand(entity)) {
            entity.removeStatusEffect(this);
            return;
        }

        if (getStacks(entity) > 10) {
            addStacks(entity, 0, 10);
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

    public static void addStacks(LivingEntity livingEntity, int amount, int maxStacks) {
        int newStacks = MathHelper.clamp(getStacks(livingEntity) + amount, 0, maxStacks);

        livingEntity.removeStatusEffect(ModEffects.FESTIVITY);
        if (newStacks > 0) {
            livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.FESTIVITY, -1, newStacks - 1, false, false));
        }
    }
}
