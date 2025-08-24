package net.freedinner.satisfying_weapons.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.UUID;

public class BrokenSoulEffect extends StatusEffect {
    public BrokenSoulEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    // Attribute modifier with a 60% value increase for movement speed
    private static final EntityAttributeModifier movementSpeedModifier = new EntityAttributeModifier(
            UUID.fromString("c783f067-92e4-40d5-8468-faa39cfea8aa"),
            "broken_soul_movement_speed_modifier",
            0.6,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    );

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        EntityAttributeInstance movementSpeedAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.addPersistentModifier(movementSpeedModifier);
        }
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        EntityAttributeInstance movementSpeedAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            movementSpeedAttribute.removeModifier(movementSpeedModifier);
        }

        super.onRemoved(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
