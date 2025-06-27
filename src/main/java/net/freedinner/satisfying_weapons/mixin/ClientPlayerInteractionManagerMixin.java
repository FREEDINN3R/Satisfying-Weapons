package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @ModifyExpressionValue(method = "method_41929", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z"))
    private boolean summonBlackHoleForHP(boolean isOnCooldown, @Local(argsOnly = true) PlayerEntity player, @Local ItemStack itemStack) {
        if (!isOnCooldown) {
            return false;
        }

        boolean canSacrificeHP = itemStack.isOf(ModItems.SWORD_OF_DYING_STAR.get(4))
                && player.getHealth() > 4
                && player.hurtTime <= 0;

        return !canSacrificeHP;
    }
}
