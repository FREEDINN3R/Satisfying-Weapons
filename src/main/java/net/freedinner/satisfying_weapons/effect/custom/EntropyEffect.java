package net.freedinner.satisfying_weapons.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class EntropyEffect extends StatusEffect {
    public EntropyEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        // Modify fall damage and defense
        ScaleData fallDamageData = ScaleTypes.FALLING.getScaleData(entity);
        fallDamageData.setScale(fallDamageData.getScale() * 1.5f * 0.8f); // Not exactly 50%, since it modifies fall distance, but close
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
        defenseData.setScale(defenseData.getScale() * 0.8f);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // TODO: Add visuals
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        // Reset fall damage and defense
        ScaleData fallDamageData = ScaleTypes.FALLING.getScaleData(entity);
        fallDamageData.setScale(fallDamageData.getScale() / 1.5f / 0.8f);
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(entity);
        defenseData.setScale(defenseData.getScale() / 0.8f);

        super.onRemoved(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
