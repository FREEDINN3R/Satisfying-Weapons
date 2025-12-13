package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrimsonKatanaSwordItem extends UpgradeableSwordItem {
    public CrimsonKatanaSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable CrimsonKatanaSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        StatusEffectInstance wither = attacker.getStatusEffect(StatusEffects.WITHER);
        if (wither != null) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, wither.getDuration(), wither.getAmplifier(), false, true), attacker);
            attacker.removeStatusEffect(StatusEffects.WITHER);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (hand == Hand.OFF_HAND || user.getItemUseTime() > 0) {
            return TypedActionResult.pass(itemStack);
        }

        if (world.isClient()) {
            return TypedActionResult.consume(itemStack);
        }

        user.damage(world.getDamageSources().playerAttack(null), 0.25f);
        user.setCurrentHand(hand);

        StatusEffectInstance wither = user.getStatusEffect(StatusEffects.WITHER);
        if (wither == null) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 160, 0));
        }
        else if (wither.getAmplifier() < 2) {
            int newAmplifier = wither.getAmplifier() + 1;
            user.removeStatusEffect(StatusEffects.WITHER);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 160, newAmplifier));
        }
        else {
            int amplifier = wither.getAmplifier();
            user.removeStatusEffect(StatusEffects.WITHER);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 160, amplifier));
        }

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }
}
