package net.freedinner.satisfying_weapons.event.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.freedinner.satisfying_weapons.PlayerWishDataManager;
import net.minecraft.server.MinecraftServer;

public class OnServerStartedWishLoot implements ServerLifecycleEvents.ServerStarted {
    @Override
    public void onServerStarted(MinecraftServer server) {
        PlayerWishDataManager.loadChestLootTables(server);
    }
}
