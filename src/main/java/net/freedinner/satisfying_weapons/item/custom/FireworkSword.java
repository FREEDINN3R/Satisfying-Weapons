package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class FireworkSword extends UpgradeableSwordItem {
    public FireworkSword(ToolMaterial toolMaterial, Settings settings, int level) {
        super(toolMaterial, settings, level);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean stacksUpdated = FestivityEffect.addStacks(attacker, 1, 10);

        if (stacksUpdated && attacker instanceof PlayerEntity playerAttacker) {
            sendParticlesPacket(playerAttacker, target);
        }

        return super.postHit(stack, target, attacker);
    }

    public static boolean heldInHand(LivingEntity entity) {
        return entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FireworkSword;
    }

    private static void sendParticlesPacket(PlayerEntity playerAttacker, LivingEntity target) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(target.getPos().toVector3f());
        buf.writeVector3f(target.getPos().subtract(playerAttacker.getPos()).toVector3f());
        buf.writeDouble(target.getWidth());
        buf.writeDouble(target.getHeight());
        buf.writeInt(FestivityEffect.getStacks(playerAttacker));

        ServerPlayNetworking.send((ServerPlayerEntity) playerAttacker, ModNetworking.FESTIVITY_GAINED_PARTICLES_ID, buf);
    }
}
