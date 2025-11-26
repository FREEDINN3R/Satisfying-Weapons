package net.freedinner.satisfying_weapons.event.handler;

import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.MechanicalSwordItem;
import net.freedinner.satisfying_weapons.mixin.LivingEntityAccessor;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class MechanicalSwordAfterDamage implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        // Only for players who were charging Mechanical Sword of Level 3-5
        if (!(entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MechanicalSwordItem mechanicalSword)
                || !entity.isUsingItem() || mechanicalSword.getLevel() < 3) {
            return;
        }

        // If not fully charged, 33% chance to add 1 charge
        if (entity.getItemUseTime() < mechanicalSword.getMaxChargeTime() && MathUtils.takeChance(0.33)) {
            int itemUseTimeLeft = entity.getItemUseTimeLeft();
            ((LivingEntityAccessor) entity).setItemUseTimeLeft(itemUseTimeLeft - mechanicalSword.getChargeRate());

            float pitch = 0.6f + 0.1f * mechanicalSword.getChargeLevel(entity.getItemUseTime());
            entity.getWorld().playSound(null, entity.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), SoundCategory.PLAYERS, 1.0f, pitch);
        }
    }
}
