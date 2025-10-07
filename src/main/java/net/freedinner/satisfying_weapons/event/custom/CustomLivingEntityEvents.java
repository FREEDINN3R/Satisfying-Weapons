package net.freedinner.satisfying_weapons.event.custom;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public final class CustomLivingEntityEvents {
    public static final Event<AfterDamage> AFTER_DAMAGE = EventFactory.createArrayBacked(AfterDamage.class, callbacks -> (entity, damageSource) -> {
        for (AfterDamage callback : callbacks) {
            callback.afterDamage(entity, damageSource);
        }
    });

    @FunctionalInterface
    public interface AfterDamage {
        /**
         * Called when a living entity actually takes damage, i.e., when damage(...) returns true
         *
         * @param entity the entity
         * @param source the source of the damage
         */
        void afterDamage(LivingEntity entity, DamageSource source);
    }
}
