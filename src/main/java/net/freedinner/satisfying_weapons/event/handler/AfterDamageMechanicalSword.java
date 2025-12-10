package net.freedinner.satisfying_weapons.event.handler;

import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.MechanicalSwordItem;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Hand;

public class AfterDamageMechanicalSword implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        // Only for players who were charging Mechanical Sword of Level 3-5
        if (!(entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MechanicalSwordItem mechanicalSword)
                || !(entity instanceof PlayerEntity player) || !player.isUsingItem() || mechanicalSword.getLevel() < 3) {
            return;
        }

        // Prevents bonus charge from own explosion
        if (source.getAttacker() == entity && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            return;
        }

        // 33% chance to try adding 1 bonus charge
        if (MathUtils.takeChance(0.33)) {
            mechanicalSword.updateChargeLevel(player, true);
        }
    }
}
