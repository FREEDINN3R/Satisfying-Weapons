package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow @Final private ExplosionBehavior behavior;

    @WrapOperation(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"))
    private boolean preventLootDestroyed(Entity entity, Operation<Boolean> original) {
        return original.call(entity) || (behavior instanceof NonDestructiveExplosionBehavior && (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity));
    }
}
