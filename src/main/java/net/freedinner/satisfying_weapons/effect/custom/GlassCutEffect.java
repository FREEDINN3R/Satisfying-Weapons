package net.freedinner.satisfying_weapons.effect.custom;

import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.freedinner.satisfying_weapons.util.ILivingEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class GlassCutEffect extends StatusEffect {
    public static final int DAMAGE_DELAY_TICKS = 5;

    public GlassCutEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);

        ((ILivingEntityDataSaver) entity).sw$setGlassCutCountdown(DAMAGE_DELAY_TICKS);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public static void applyDamageTo(LivingEntity target) {
        // Prevents players revived by totems from immediately dying again
        // Technically I could leave it, but that would be too OP
        if (target.getWorld().isClient() || !target.hasStatusEffect(ModEffects.GLASS_CUT)) {
            return;
        }

        // Inherits the attacker if the original instance of damage had one
        LivingEntity lastAttacker = null;
        if (target.getLastAttacker() != null && target.age - target.getLastAttackedTime() <= DAMAGE_DELAY_TICKS + 1) {
            lastAttacker = target.getLastAttacker();
        }

        DamageSource glassCutDamageSource = CombatHelper.getDamageSource(ModDamageTypes.GLASS_CUT, target.getWorld(), lastAttacker);

        // Damage = 2 * lvl
        float amplifier = target.getStatusEffect(ModEffects.GLASS_CUT).getAmplifier();
        target.damage(glassCutDamageSource, 2 * (amplifier + 1));
    }
}
