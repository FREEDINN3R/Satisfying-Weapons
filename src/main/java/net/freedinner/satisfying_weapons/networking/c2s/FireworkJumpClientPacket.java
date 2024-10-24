package net.freedinner.satisfying_weapons.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FireworkJumpClientPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            // Removing 3 stacks
            FestivityEffect.addStacks(player, -3, 10);

            // Increasing Firework Jump effect by 1
            int currLevel = player.hasStatusEffect(ModEffects.FIREWORK_JUMP) ? player.getStatusEffect(ModEffects.FIREWORK_JUMP).getAmplifier() : -1;
            player.removeStatusEffect(ModEffects.FIREWORK_JUMP);
            player.addStatusEffect(new StatusEffectInstance(ModEffects.FIREWORK_JUMP, 1200, currLevel + 1, false, false));
        });
    }
}
