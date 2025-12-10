package net.freedinner.satisfying_weapons.event.handler;

import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.effect.custom.GlassCutEffect;
import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.util.data.ILivingEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class AfterDamageGlassCut implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        if (!entity.hasStatusEffect(ModEffects.GLASS_CUT) || source.isOf(ModDamageTypes.GLASS_CUT)) {
            return;
        }

        ILivingEntityDataSaver entityDataSaver = (ILivingEntityDataSaver) entity;
        int glassCutCountdown = entityDataSaver.sw$getGlassCutCountdown();

        if (glassCutCountdown <= 0) {
            entityDataSaver.sw$setGlassCutCountdown(GlassCutEffect.DAMAGE_DELAY_TICKS);
        }
    }
}
