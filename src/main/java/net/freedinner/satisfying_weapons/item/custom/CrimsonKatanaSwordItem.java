package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.CombatHelper;
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
        World world = target.getWorld();

        // Poison goes first because it's non-lethal
        StatusEffectInstance poison = attacker.getStatusEffect(StatusEffects.POISON);
        if (poison != null && this.getLevel() >= 2) {
            int[] dmgRate = {25, 12, 6, 3, 1};
            int amplifier = Math.min(4, poison.getAmplifier());
            float totalDamage = poison.getDuration() / dmgRate[amplifier];
            totalDamage = Math.min(totalDamage, target.getHealth() - 1);

            target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_POISON, world), totalDamage);
            attacker.removeStatusEffect(StatusEffects.POISON);
        }

        StatusEffectInstance wither = attacker.getStatusEffect(StatusEffects.WITHER);
        if (wither != null) {
            int[] dmgRate = {40, 20, 10, 5, 2, 1};
            int amplifier = Math.min(5, wither.getAmplifier());
            int totalDamage = wither.getDuration() / dmgRate[amplifier];

            target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_WITHER, world), totalDamage);
            attacker.removeStatusEffect(StatusEffects.WITHER);
        }

        if (target.isOnFire() && this.getLevel() >= 4) {
            int dmgRate = 20;
            int totalDamage = target.getFireTicks() / dmgRate;

            target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_BURN, world), totalDamage);
            attacker.setFireTicks(0);
        }

        return super.postHit(stack, target, attacker);
    }
}
