package net.freedinner.satisfying_weapons.mixin;

import com.mojang.authlib.GameProfile;
import net.freedinner.satisfying_weapons.util.IPlayerDataSaver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IPlayerDataSaver {
    @Unique
    private final static String LAST_DROP_TIME_NBT_KEY = "sw_last_drop_time";

    @Unique
    private long lastDropTime;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        lastDropTime = world.getTime();
    }

    @Override
    public long getLastDropTime() {
        return lastDropTime;
    }

    @Override
    public void setLastDropTime(long worldTick) {
        lastDropTime = worldTick;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putLong(LAST_DROP_TIME_NBT_KEY, lastDropTime);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(LAST_DROP_TIME_NBT_KEY)) {
            lastDropTime = nbt.getLong(LAST_DROP_TIME_NBT_KEY);
        }
    }
}
