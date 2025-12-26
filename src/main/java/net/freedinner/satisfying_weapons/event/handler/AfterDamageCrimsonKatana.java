package net.freedinner.satisfying_weapons.event.handler;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.CrimsonKatanaItem;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.freedinner.satisfying_weapons.util.data.ILivingEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AfterDamageCrimsonKatana implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        Entity attacker = source.getAttacker();

        if (!(attacker instanceof PlayerEntity playerAttacker)) {
            return;
        }

        CrimsonKatanaItem katanaItem = searchHotbarForKatana(playerAttacker);

        if (katanaItem == null) {
            return;
        }

        ArrayList<CrimsonKatanaItem.DoT> possibleDots = CrimsonKatanaItem.DoT.getDotsForLevel(katanaItem.getLevel());
        ArrayList<CrimsonKatanaItem.DoT> chosenDots = new ArrayList<>();

        int count = (katanaItem.getLevel() >= 4) ? 2 : 1;
        for (int i = 0; i < count; i++) {
            chosenDots.add(MathUtils.randomElementFrom(possibleDots, true));
        }

        ((ILivingEntityDataSaver) entity).sw$scheduleDots(chosenDots);
    }

    public static void inflictScheduledDots(LivingEntity entity, List<CrimsonKatanaItem.DoT> scheduledDots) {
        for (CrimsonKatanaItem.DoT dotEffect : scheduledDots) {
            dotEffect.inflictOn(entity);
        }

        sendParticlesPacket(entity, scheduledDots);
    }

    @Nullable
    private static CrimsonKatanaItem searchHotbarForKatana(PlayerEntity playerAttacker) {
        PlayerInventory inventory = playerAttacker.getInventory();
        CrimsonKatanaItem katanaItem = null;

        ItemStack offhandStack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
        if (offhandStack.getItem() instanceof CrimsonKatanaItem foundKatanaItem) {
            katanaItem = foundKatanaItem;
        }

        for (int i = 0; PlayerInventory.isValidHotbarIndex(i); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof CrimsonKatanaItem foundKatanaItem
            && (katanaItem == null || katanaItem.getLevel() < foundKatanaItem.getLevel())) {
                katanaItem = foundKatanaItem;
            }
        }

        return katanaItem;
    }

    private static void sendParticlesPacket(LivingEntity entity, List<CrimsonKatanaItem.DoT> inflictedDots) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumSet(EnumSet.copyOf(inflictedDots), CrimsonKatanaItem.DoT.class);
        buf.writeVector3f(PosUtils.getEntityCenter(entity).toVector3f());

        for (ServerPlayerEntity player : PosUtils.getPlayersTracking(entity)) {
            PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
            bufCopy.writeVector3f(player.getEyePos().toVector3f());

            ServerPlayNetworking.send(player, ModNetworking.DOT_INFLICT_PARTICLES_ID, bufCopy);
        }
    }
}
