package net.freedinner.satisfying_weapons.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.event.handler.BrokenSoulServerSync;
import net.freedinner.satisfying_weapons.event.handler.GlassCutAfterDamage;
import net.freedinner.satisfying_weapons.event.handler.GlassSwordPreventDeath;

public class ModEvents {
    public static void registerEvents() {
        SatisfyingWeapons.LOGGER.info("Registering server-side events");

        ServerLivingEntityEvents.ALLOW_DEATH.register(new GlassSwordPreventDeath());
        CustomLivingEntityEvents.AFTER_DAMAGE.register(new GlassCutAfterDamage());
        ServerTickEvents.END_SERVER_TICK.register(new BrokenSoulServerSync());
    }

    public static void registerEventsClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side events");
    }
}
