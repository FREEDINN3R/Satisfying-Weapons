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
    private final static String LAST_DROP_TIME_NBT_KEY = "satisfying_weapons_last_drop_time";
    @Unique
    private final static String ON_GROUND_TIME_FS_NBT_KEY = "satisfying_weapons_on_ground_time_fs";

    @Unique
    private long lastDropTime;
    @Unique
    private int onGroundTimeFS;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        lastDropTime = world.getTime();
        onGroundTimeFS = 0;
    }

    @Override
    public long sw$getLastDropTime() {
        return lastDropTime;
    }

    @Override
    public void sw$setLastDropTime(long worldTick) {
        lastDropTime = worldTick;
    }

    @Override
    public int sw$getOnGroundTimeFS() {
        return onGroundTimeFS;
    }

    @Override
    public void sw$setOnGroundTimeFS(int time) {
        onGroundTimeFS = time;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putLong(LAST_DROP_TIME_NBT_KEY, lastDropTime);
        nbt.putInt(ON_GROUND_TIME_FS_NBT_KEY, onGroundTimeFS);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(LAST_DROP_TIME_NBT_KEY)) {
            lastDropTime = nbt.getLong(LAST_DROP_TIME_NBT_KEY);
        }

        if (nbt.contains(ON_GROUND_TIME_FS_NBT_KEY)) {
            onGroundTimeFS = nbt.getInt(ON_GROUND_TIME_FS_NBT_KEY);
        }
    }
}
