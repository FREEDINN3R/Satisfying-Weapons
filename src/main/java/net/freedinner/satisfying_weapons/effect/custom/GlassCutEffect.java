package net.freedinner.satisfying_weapons.effect.custom;

import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class GlassCutEffect extends StatusEffect {
    public static final int DAMAGE_DELAY_TICKS = 5;

    public GlassCutEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static void applyDamageTo(LivingEntity target) {
        if (target.getWorld().isClient() || !target.hasStatusEffect(ModEffects.GLASS_CUT)) {
            return;
        }

        LivingEntity lastAttacker = null;
        if (target.getLastAttacker() != null && target.age - target.getLastAttackedTime() <= DAMAGE_DELAY_TICKS + 1) {
            lastAttacker = target.getLastAttacker();
        }

        DamageSource glassCutDamageSource = CombatHelper.getDamageSource(ModDamageTypes.GLASS_CUT, target.getWorld(), lastAttacker);

        float amplifier = target.getStatusEffect(ModEffects.GLASS_CUT).getAmplifier();
        target.damage(glassCutDamageSource, 2 * (amplifier + 1));
    }
}
