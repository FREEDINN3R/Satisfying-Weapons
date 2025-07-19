package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @ModifyExpressionValue(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;isCoolingDown(Lnet/minecraft/item/Item;)Z"))
    private boolean summonBlackHoleForHP(boolean isOnCooldown, @Local(argsOnly = true) ServerPlayerEntity player, @Local(argsOnly = true) ItemStack itemStack) {
        if (!isOnCooldown) {
            return false;
        }

        // Can sacrifice if has enough HP and no invincibility frames
        boolean canSacrificeHP = itemStack.isOf(ModItems.SWORD_OF_DYING_STAR.get(4))
                && player.getHealth() > 4
                && player.hurtTime <= 0;

        if (canSacrificeHP && !player.isInvulnerable() && !player.isCreative()) {
            player.damage(player.getWorld().getDamageSources().outOfWorld(), 4);

            if (player.getHealth() <= 1) {
                player.setHealth(1);
            }
        }

        return !canSacrificeHP;
    }
}
