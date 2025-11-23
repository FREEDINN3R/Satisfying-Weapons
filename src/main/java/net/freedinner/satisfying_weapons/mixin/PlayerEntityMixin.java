package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.freedinner.satisfying_weapons.item.custom.MechanicalSwordItem;
import net.freedinner.satisfying_weapons.util.data.IPlayerDataSaver;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
    private final static String ON_GROUND_TIME_FS_NBT_KEY = "sw_on_ground_time_fs";

    @Unique
    private long lastDropTime;
    @Unique
    private int onGroundTimeFS;
    @Unique
    private int energyChargeMS; // Does not require nbt saving

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        lastDropTime = world.getTime();
        onGroundTimeFS = 0;
        energyChargeMS = MechanicalSwordItem.STARTING_CHARGE;
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getKnockback(Lnet/minecraft/entity/LivingEntity;)I"))
    private int increaseKnockbackMS(int original) {
        ItemStack stackInHand = ((PlayerEntity)(Object)this).getMainHandStack();
        return (stackInHand.getItem() instanceof MechanicalSwordItem) ? original + 1 : original;
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

    @Override
    public int sw$getChargeMS() {
        return energyChargeMS;
    }
    @Override
    public void sw$setChargeMS(int i) {
        energyChargeMS = i;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putLong(LAST_DROP_TIME_NBT_KEY, lastDropTime);
        nbt.putInt(ON_GROUND_TIME_FS_NBT_KEY, onGroundTimeFS);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        lastDropTime = NbtUtils.getOrCreate(nbt, LAST_DROP_TIME_NBT_KEY, 0L);
        onGroundTimeFS = NbtUtils.getOrCreate(nbt, ON_GROUND_TIME_FS_NBT_KEY, 0);
    }
}
