package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;

public class FireworkSwordItem extends SwordItem {
    public FireworkSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        FestivityEffect.addStacks(attacker, 1, 10);
        return super.postHit(stack, target, attacker);
    }

    public static boolean heldInHand(LivingEntity entity) {
        return entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FireworkSwordItem;
    }
}
