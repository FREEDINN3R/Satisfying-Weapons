package net.freedinner.satisfying_weapons.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.event.handler.*;

public class ModEvents {
    public static void registerEvents() {
        SatisfyingWeapons.LOGGER.info("Registering server-side events");

        ServerLifecycleEvents.SERVER_STARTED.register(new OnServerStartedWishLoot());
        ServerLivingEntityEvents.ALLOW_DEATH.register(new PreventDeathGlassSword());
        CustomLivingEntityEvents.AFTER_DAMAGE.register(new AfterDamageGlassCut());
        CustomLivingEntityEvents.AFTER_DAMAGE.register(new AfterDamageCrimsonKatana());
        CustomLivingEntityEvents.AFTER_DAMAGE.register(new AfterDamageMechanicalSword());
    }

    public static void registerEventsClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side events");
    }
}
