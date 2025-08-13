package net.freedinner.satisfying_weapons.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.event.custom.GlassCutDamageHandler;
import net.freedinner.satisfying_weapons.event.custom.GlassSwordDeathHandler;

public class ModEvents {
    public static void registerEvents() {
        SatisfyingWeapons.LOGGER.info("Registering server-side events");

        ServerLivingEntityEvents.ALLOW_DEATH.register(new GlassSwordDeathHandler());
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(new GlassCutDamageHandler());
    }

    public static void registerEventsClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side events");
    }
}
