package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.effect.custom.GlassCutEffect;
import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.util.ILivingEntityDataSaver;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityDataSaver {
    @Unique
    private int glassCutCountdown;
    @Unique
    private int brokenSoulSwordLevel;
    @Unique
    private int dropAttemptsBH;

    @Unique
    private final static String GLASS_CUT_COUNTDOWN_NBT_KEY = "sw_glass_cut_countdown";
    @Unique
    private final static String BROKEN_SOUL_SWORD_LEVEL_NBT_KEY = "sw_broken_soul_sword_level";
    @Unique
    private final static String DROP_ATTEMPTS_BH_NBT_KEY = "sw_drop_attempts_bh";

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructor(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        this.glassCutCountdown = 0;
        this.brokenSoulSwordLevel = 0;
        this.dropAttemptsBH = 0;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (this.getWorld().isClient) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;

        // Counts down the delay for GC after receiving damage
        int gcCountdown = this.sw$getGlassCutCountdown();
        if (gcCountdown > 0) {
            this.sw$setGlassCutCountdown(--gcCountdown);
            if (gcCountdown == 0) {
                GlassCutEffect.applyDamageTo(entity);
            }
        }

        // Equipment drop attempts for BH; takes approx. 60 seconds to reset to 0 (players only)
        int dropAttempts = this.sw$getDropAttemptsBH();
        if (dropAttempts > 0 && entity instanceof PlayerEntity && MathUtils.takeChance(0.05)) {
            this.sw$setDropAttemptsBH(dropAttempts - 1);
        }
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void reduceHealGlassCut(float amount, CallbackInfo ci, @Local(argsOnly = true) LocalFloatRef mutableAmount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        StatusEffectInstance glassCut = entity.getStatusEffect(ModEffects.GLASS_CUT);

        if (glassCut != null) {
            float multiplier = switch(glassCut.getAmplifier()) {
                case 0 -> 1f;
                case 1 -> 0.75f;
                default -> 0.5f;
            };

            mutableAmount.set(amount * multiplier);
        }
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void preventHealBrokenSoul(float amount, CallbackInfo ci, @Local(argsOnly = true) LocalFloatRef mutableAmount) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasStatusEffect(ModEffects.BROKEN_SOUL)) {
            mutableAmount.set(0f);
        }
    }

    @Inject(method = "setAbsorptionAmount", at = @At("HEAD"))
    private void preventAbsorptionBrokenSoul(float amount, CallbackInfo ci, @Local(argsOnly = true) LocalFloatRef mutableAmount) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.hasStatusEffect(ModEffects.BROKEN_SOUL)) {
            mutableAmount.set(0f);
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void afterDamageEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            CustomLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage((LivingEntity)(Object) this, source);
        }
    }

    @ModifyExpressionValue(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 7))
    private boolean preventKnockbackFromGlassCut(boolean original, @Local(argsOnly = true) DamageSource source) {
        return original || source.isOf(ModDamageTypes.GLASS_CUT);
    }

    @Redirect(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;timeUntilRegen:I", opcode = Opcodes.PUTFIELD))
    private void preventIFramesFromGlassCut(LivingEntity instance, int value, @Local(argsOnly = true) DamageSource source) {
        if (!source.isOf(ModDamageTypes.GLASS_CUT)) {
            instance.timeUntilRegen = value;
        }
    }

    @Redirect(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;lastDamageTaken:F", opcode = Opcodes.PUTFIELD))
    private void preventLastDamageFromGlassCut(LivingEntity instance, float value, @Local(argsOnly = true) DamageSource source) {
        if (!source.isOf(ModDamageTypes.GLASS_CUT)) {
            ((LivingEntityAccessor) instance).setLastDamageTaken(value);
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
    public int sw$getBrokenSoulSwordLevel() {
        return brokenSoulSwordLevel;
    }

    @Override
    public void sw$setBrokenSoulSwordLevel(int level) {
        brokenSoulSwordLevel = level;
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
        nbt.putInt(BROKEN_SOUL_SWORD_LEVEL_NBT_KEY, brokenSoulSwordLevel);
        nbt.putInt(DROP_ATTEMPTS_BH_NBT_KEY, dropAttemptsBH);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        glassCutCountdown = NbtUtils.getOrCreate(nbt, GLASS_CUT_COUNTDOWN_NBT_KEY, 0);
        brokenSoulSwordLevel = NbtUtils.getOrCreate(nbt, BROKEN_SOUL_SWORD_LEVEL_NBT_KEY, 0);
        dropAttemptsBH = NbtUtils.getOrCreate(nbt, DROP_ATTEMPTS_BH_NBT_KEY, 0);
    }
}
