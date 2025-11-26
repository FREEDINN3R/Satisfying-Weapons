package net.freedinner.satisfying_weapons.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.UUID;

public class MetalHeartEffect extends StatusEffect {

    // Attribute modifier with flat 40% value increase for knockback resistance
    private static final EntityAttributeModifier knockbackResModifier = new EntityAttributeModifier(
            UUID.fromString("409b4ef2-4024-4461-ab6e-bf490695aa19"),
            "Custom knockback resistance",
            0.4,
            EntityAttributeModifier.Operation.ADDITION
    );

    public MetalHeartEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // 70% all damage resistance
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(player);
        defenseData.setScale(defenseData.getScale() / 0.3f);

        // Knockback resistance
        EntityAttributeInstance knockbackResAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        if (knockbackResAttribute != null) {
            knockbackResAttribute.addPersistentModifier(knockbackResModifier);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Remove damage resistance
        ScaleData defenseData = ScaleTypes.DEFENSE.getScaleData(player);
        defenseData.setScale(defenseData.getScale() * 0.3f);

        // Remove knockback resistance
        EntityAttributeInstance knockbackResAttribute = entity.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        if (knockbackResAttribute != null) {
            knockbackResAttribute.removeModifier(knockbackResModifier);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
