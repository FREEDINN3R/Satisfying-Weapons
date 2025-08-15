package net.freedinner.satisfying_weapons.mixin;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.custom.GlassCutEffect;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.freedinner.satisfying_weapons.util.ILivingEntityDataSaver;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityDataSaver {

    @Unique
    private int glassCutCountdown;
    @Unique
    private int dropAttemptsBH;

    @Unique
    private final static String GLASS_CUT_COUNTDOWN_NBT_KEY = "satisfying_weapons_glass_cut_countdown";
    @Unique
    private final static String DROP_ATTEMPTS_BH_NBT_KEY = "satisfying_weapons_drop_attempts_bh";

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        this.glassCutCountdown = 0;
        this.dropAttemptsBH = 0;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.getWorld().isClient) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        int glassCutCountdown = this.sw$getGlassCutCountdown();
        if (glassCutCountdown > 0) {
            this.sw$setGlassCutCountdown(--glassCutCountdown);

            if (glassCutCountdown == 0) {
                DamageSource glassCutDamageSource;

                if (entity.getLastAttacker() != null && entity.age - entity.getLastAttackedTime() <= GlassCutEffect.DAMAGE_DELAY_TICKS + 1) {
                    glassCutDamageSource = CombatHelper.getDamageSource(ModDamageTypes.GLASS_CUT, entity.getWorld(), entity.getLastAttacker());
                }
                else {
                    glassCutDamageSource = CombatHelper.getDamageSource(ModDamageTypes.GLASS_CUT, entity.getWorld(), null);
                }

                entity.damage(glassCutDamageSource, 2);
            }
        }

        // Takes approx. 60 seconds to reach 0
        int dropAttempts = this.sw$getDropAttemptsBH();
        if (dropAttempts > 0 && entity instanceof PlayerEntity && MathUtils.takeChance(0.05)) {
            this.sw$setDropAttemptsBH(dropAttempts - 1);
        }
    }

    @Override
    public int sw$getGlassCutCountdown() {
        return glassCutCountdown;
    }

    @Override
    public void sw$setGlassCutCountdown(int ticks) {
        glassCutCountdown = ticks;
    }

    @Override
    public int sw$getDropAttemptsBH() {
        return dropAttemptsBH;
    }

    @Override
    public void sw$setDropAttemptsBH(int amount) {
        dropAttemptsBH = amount;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt(GLASS_CUT_COUNTDOWN_NBT_KEY, glassCutCountdown);
        nbt.putInt(DROP_ATTEMPTS_BH_NBT_KEY, dropAttemptsBH);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        glassCutCountdown = NbtUtils.getOrCreate(nbt, GLASS_CUT_COUNTDOWN_NBT_KEY, 0);
        dropAttemptsBH = NbtUtils.getOrCreate(nbt, DROP_ATTEMPTS_BH_NBT_KEY, 0);
    }
}
