package net.freedinner.satisfying_weapons.event.handler;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.GlassSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

public class GlassSwordAllowDeath implements ServerLivingEntityEvents.AllowDeath {
    @Override
    public boolean allowDeath(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        if (damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }

        return !GlassSwordItem.tryBreakSword(entity);
    }
}
