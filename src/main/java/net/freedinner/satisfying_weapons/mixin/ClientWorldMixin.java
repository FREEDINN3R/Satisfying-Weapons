package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @WrapOperation(method = "addFireworkParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addParticle(Lnet/minecraft/client/particle/Particle;)V"))
    private void onAddParticle(ParticleManager instance, Particle particle, Operation<Void> original, @Local(argsOnly = true) NbtCompound nbt) {
        SatisfyingWeapons.LOGGER.info(nbt.toString());
        original.call(instance, particle);
    }
}
