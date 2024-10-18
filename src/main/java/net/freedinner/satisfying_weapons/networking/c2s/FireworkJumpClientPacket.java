package net.freedinner.satisfying_weapons.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class FireworkJumpClientPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            // Removing 3 stacks
            FestivityEffect.addStacks(player, -3, 10);

            // Increasing Firework Jump effect by 1
            int currLevel = player.hasStatusEffect(ModEffects.FIREWORK_JUMP) ? player.getStatusEffect(ModEffects.FIREWORK_JUMP).getAmplifier() : -1;
            player.removeStatusEffect(ModEffects.FIREWORK_JUMP);
            player.addStatusEffect(new StatusEffectInstance(ModEffects.FIREWORK_JUMP, -1, currLevel + 1, false, false));
        });
    }
}
