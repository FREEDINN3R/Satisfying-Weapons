package net.freedinner.satisfying_weapons.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.freedinner.satisfying_weapons.effect.custom.FireworkJumpEffect;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Unique
    private boolean jumpedLastTick = false;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickMovement(CallbackInfo info) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        // If eligible player pressed jump
        if (player.input.jumping && !jumpedLastTick && FireworkJumpEffect.isEligible(player, true)) {
            Vec3d v = player.getVelocity();
            player.setVelocity(v.x, 1.5, v.z);
            player.velocityModified = true;

            ClientPlayNetworking.send(ModNetworking.FIREWORK_JUMP_CLIENT_PACKET, PacketByteBufs.create());
        }

        jumpedLastTick = player.input.jumping;
    }
}