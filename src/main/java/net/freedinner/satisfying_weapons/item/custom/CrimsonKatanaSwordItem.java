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
            int[] dmgRates = {40, 20, 10, 5, 2, 1};
            int dmgRate = (wither.getAmplifier() > 5) ? dmgRates[5] : dmgRates[wither.getAmplifier()];
            int totalDamage = wither.getDuration() / dmgRate;

            target.damage(target.getDamageSources().wither(), totalDamage);
            attacker.removeStatusEffect(StatusEffects.WITHER);
        }

        return super.postHit(stack, target, attacker);
    }
}
