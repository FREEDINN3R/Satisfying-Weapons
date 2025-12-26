package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.mixin.LivingEntityAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class SyncUseTimeLeftPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        int itemUseTimeLeft = buf.readInt();

        client.execute(() -> {
            ClientPlayerEntity player = client.player;

            if (player == null) {
                return;
            }

            ((LivingEntityAccessor) player).setItemUseTimeLeft(itemUseTimeLeft);
        });
    }
}
