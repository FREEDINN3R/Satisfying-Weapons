package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworksSparkParticle.FireworkParticle.class)
public abstract class FireworkParticleMixin {
    @Shadow
    private int age;

    @Shadow
    private NbtList explosions;

    @Unique
    private boolean grounded = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initGroundedValue(CallbackInfo ci, @Local(argsOnly = true) NbtCompound nbt) {
        if (nbt != null) {
            grounded = nbt.getBoolean("Grounded");
        }
    }

    @ModifyVariable(method = "explodeBall", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int reduceParticleCount(int amount) {
        boolean hasTrail = explosions.getCompound(age / 2).getBoolean("Trail");
        return (grounded && hasTrail) ? Math.max(1, amount / 2) : amount;
    }

    @WrapOperation(method = "explodeBall", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/FireworksSparkParticle$FireworkParticle;addExplosionParticle(DDDDDD[I[IZZ)V"))
    private void preventGroundClipping(FireworksSparkParticle.FireworkParticle instance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int[] colors, int[] fadeColors, boolean trail, boolean flicker, Operation<Void> original) {
        // Some more particle count reduction
        if (grounded && (velocityY < 0 || MathUtils.takeChance(0.4))) {
            return;
        }

        original.call(instance, x, y, z, velocityX, velocityY, velocityZ, colors, fadeColors, trail, flicker);
    }
}
