package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GlassSwordItem extends UpgradeableSwordItem {
    private static final String GLASS_STATE_NBT_KEY = "glass_state";

    public GlassSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable GlassSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.getDamage() > 1 && getGlassState(stack) != GlassState.BROKEN) {
            setGlassState(stack, GlassState.BROKEN);

            attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, PitchUtils.get());
            this.sendParticlesPacket(attacker);
        }

        return super.postHit(stack, target, attacker);
    }

    public static GlassState getGlassState(ItemStack itemStack) {
        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        int stateId = stackNbt.getInt(GLASS_STATE_NBT_KEY); // if doesn't exist, returns 0 by default

        return GlassState.values()[stateId];
    }

    public static void setGlassState(ItemStack itemStack, GlassState state) {
        NbtCompound stackNbt = itemStack.getOrCreateNbt();
        stackNbt.putInt(GLASS_STATE_NBT_KEY, state.ordinal());
    }

    private void sendParticlesPacket(LivingEntity attacker) {
        Vector3f particlePos = attacker.getPos().toVector3f();
        particlePos.add(0, attacker.getHeight() * 0.3f, 0);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(particlePos);

        BlockPos blockPos = PosUtils.toBlockPos(attacker.getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)attacker.getWorld(), blockPos)) {
            ServerPlayNetworking.send(player, ModNetworking.GLASS_SWORD_PARTICLES_ID, buf);
        }
    }

    public enum GlassState {
        INTACT,
        CRACKED,
        BROKEN
    }
}
