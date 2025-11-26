package net.freedinner.satisfying_weapons.event.handler;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.MechanicalSwordItem;
import net.freedinner.satisfying_weapons.mixin.LivingEntityAccessor;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class MechanicalSwordAfterDamage implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        // Only for players who were charging Mechanical Sword of Level 3-5
        if (!(entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MechanicalSwordItem mechanicalSword)
                || !(entity instanceof PlayerEntity player) || !player.isUsingItem() || mechanicalSword.getLevel() < 3) {
            return;
        }

        // If not fully charged, 33% chance to add 1 charge
        if (player.getItemUseTime() < mechanicalSword.getMaxChargeTime() && MathUtils.takeChance(0.33)) {
            int itemUseTimeLeft = player.getItemUseTimeLeft();
            itemUseTimeLeft -= mechanicalSword.getChargeRate();
            ((LivingEntityAccessor) player).setItemUseTimeLeft(itemUseTimeLeft);

            mechanicalSword.playChargeSound(player);

            // Syncing itemUseTimeLeft with client
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(itemUseTimeLeft);
            ServerPlayNetworking.send((ServerPlayerEntity) player, ModNetworking.SYNC_USE_TIME_LEFT_ID, buf);
        }
    }
}
