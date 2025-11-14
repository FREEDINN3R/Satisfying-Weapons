package net.freedinner.satisfying_weapons.gui;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlers {
    public static final ScreenHandlerType<UpgraderScreenHandler> UPGRADER_SCREEN_HANDLER =
            ScreenHandlerRegistry.registerSimple(
                    SatisfyingWeapons.id("upgrader_screen_handler"),
                    UpgraderScreenHandler::new
            );
    public static final ScreenHandlerType<DeconstructorScreenHandler> DECONSTRUCTOR_SCREEN_HANDLER =
            ScreenHandlerRegistry.registerSimple(
                    SatisfyingWeapons.id("deconstructor_screen_handler"),
                    DeconstructorScreenHandler::new
            );

    public static void registerScreenHandlers() {
        SatisfyingWeapons.LOGGER.info("Registering screen handlers");
    }

    public static void registerScreenHandlersClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side screen handlers");

        HandledScreens.register(UPGRADER_SCREEN_HANDLER, UpgraderScreen::new);
        HandledScreens.register(DECONSTRUCTOR_SCREEN_HANDLER, DeconstructorScreen::new);
    }
}
