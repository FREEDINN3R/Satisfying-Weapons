package net.freedinner.satisfying_weapons.event.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.item.custom.GlassSwordItem;
import net.freedinner.satisfying_weapons.util.ILivingEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

public class GlassCutDamageHandler implements ServerLivingEntityEvents.AllowDamage {
    @Override
    public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        // TODO: allowDamage is unreliable, since other mods may cancel it; use mixins instead
        if (entity.hasStatusEffect(ModEffects.GLASS_CUT)) {
            ILivingEntityDataSaver entityDataSaver = (ILivingEntityDataSaver) entity;
            int glassCutCountdown = entityDataSaver.sw$getGlassCutCountdown();

            if (!source.isOf(ModDamageTypes.GLASS_CUT) && glassCutCountdown <= 0) {
                entityDataSaver.sw$setGlassCutCountdown(10);
            }
        }

        return true;
    }
}
