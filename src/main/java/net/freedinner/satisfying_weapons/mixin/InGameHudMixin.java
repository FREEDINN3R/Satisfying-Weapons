package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @ModifyExpressionValue(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getHealth()F"))
    private float brokenSoulRenderZeroHP(float original, @Local PlayerEntity player) {
        if (player.hasStatusEffect(ModEffects.BROKEN_SOUL)) {
            return 0;
        }

        return original;
    }
}
