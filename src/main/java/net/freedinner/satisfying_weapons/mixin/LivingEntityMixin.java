package net.freedinner.satisfying_weapons.mixin;

import net.freedinner.satisfying_weapons.util.ILivingEntityDataSaver;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntityDataSaver {
    @Unique
    private final static String DROP_ATTEMPTS_BH_NBT_KEY = "satisfying_weapons_drop_attempts_bh";
    @Unique
    private int dropAttemptsBH;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        this.dropAttemptsBH = 0;
    }

    @Override
    public int satisfyingWeapons$getDropAttemptsBH() {
        return dropAttemptsBH;
    }

    @Override
    public void satisfyingWeapons$addDropAttemptsBH(int x) {
        dropAttemptsBH += x;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt(DROP_ATTEMPTS_BH_NBT_KEY, dropAttemptsBH);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(DROP_ATTEMPTS_BH_NBT_KEY)) {
            dropAttemptsBH = nbt.getInt(DROP_ATTEMPTS_BH_NBT_KEY);
        }
    }
}
