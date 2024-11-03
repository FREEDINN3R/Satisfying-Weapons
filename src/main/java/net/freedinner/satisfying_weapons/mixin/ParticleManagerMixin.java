package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.freedinner.satisfying_weapons.particle.custom.FestivityCountParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Unique
    private static boolean renderThroughBlocks = false;

    @WrapOperation(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    private void checkParticleType(Particle instance, VertexConsumer vertexConsumer, Camera camera, float tickDelta, Operation<Void> original) {
        if (instance instanceof FestivityCountParticle) {
            renderThroughBlocks = true;
        }

        original.call(instance, vertexConsumer, camera, tickDelta);
    }

    @WrapOperation(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleTextureSheet;draw(Lnet/minecraft/client/render/Tessellator;)V"))
    private void wrapParticleDraw(ParticleTextureSheet instance, Tessellator tessellator, Operation<Void> original) {
        if (renderThroughBlocks) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
        }

        original.call(instance, tessellator);

        renderThroughBlocks = false;
    }
}
