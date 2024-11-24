package net.freedinner.satisfying_weapons.entity.custom;

import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ToyArrowEntity extends PersistentProjectileEntity {
    private static final TrackedData<Integer> TOY_BOW_LEVEL = DataTracker.registerData(ToyArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String TOY_BOW_LEVEL_NBT_KEY = "toy_bow_level";
    private static final String CAN_HIT_OWNER_NBT_KEY = "can_hit_owner";
    public boolean canHitOwner = true;

    public ToyArrowEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ToyArrowEntity(LivingEntity owner, World world) {
        super(ModEntities.TOY_ARROW, owner, world);
    }

    public ToyArrowEntity(Vec3d pos, World world) {
        super(ModEntities.TOY_ARROW, pos.x, pos.y, pos.z, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(TOY_BOW_LEVEL, 1);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        // If this hit wasn't fatal, apply Birthday Party
        if (target.isAlive()) {
            tryApplyingBirthdayParty(target);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        // Can only hit owner if this is the original arrow
        if (!canHitOwner && hitEntity == this.getOwner()) {
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.setYaw(this.getYaw() + 180.0f);
            this.prevYaw += 180.0f;
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 0.0000001) {
                this.discard();
            }
        }
        else {
            super.onEntityHit(entityHitResult);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean(CAN_HIT_OWNER_NBT_KEY, canHitOwner);
        nbt.putInt(TOY_BOW_LEVEL_NBT_KEY, this.getToyBowLevel());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(CAN_HIT_OWNER_NBT_KEY)) {
            canHitOwner = nbt.getBoolean(CAN_HIT_OWNER_NBT_KEY);
        }

        if (nbt.contains(TOY_BOW_LEVEL_NBT_KEY)) {
            this.setToyBowLevel(nbt.getInt(TOY_BOW_LEVEL_NBT_KEY));
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.ARROW);
    }

    public int getToyBowLevel() {
        return this.dataTracker.get(TOY_BOW_LEVEL);
    }

    public void setToyBowLevel(int level) {
        this.dataTracker.set(TOY_BOW_LEVEL, level);
    }

    private void tryApplyingBirthdayParty(LivingEntity target) {
        // Note to self: all this stuff needs to be here because only Toy Arrow can cause it

        int thisEffectLevel = (this.getToyBowLevel() >= 4) ? 1 : 0;
        int existingEffectLevel = (target.hasStatusEffect(ModEffects.BIRTHDAY_PARTY)) ?
                target.getStatusEffect(ModEffects.BIRTHDAY_PARTY).getAmplifier() : -1;

        // If already has Birthday Party of the same / greater level, do nothing
        if (existingEffectLevel >= thisEffectLevel) {
            return;
        }

        // If already has Birthday Party of a lesser level, simply upgrade it
        if (existingEffectLevel != -1) {
            changeBirthdayPartyLevel(target, thisEffectLevel);
            return;
        }

        // Apply Birthday Party for 10 seconds
        StatusEffectInstance birthdayPartyEffect = new StatusEffectInstance(ModEffects.BIRTHDAY_PARTY, 200, thisEffectLevel, false, false);
        boolean success = target.addStatusEffect(birthdayPartyEffect, this.getEffectCause());

        if (!success) {
            return;
        }

        if (this.getToyBowLevel() >= 3) {
            summonBirthdayGift(target);
        }

        // Visuals & SFX
        target.getWorld().playSound(null, target.getBlockPos(), ModSounds.PARTY_HORN, SoundCategory.PLAYERS, 3.0f, PitchUtils.get());
        target.getWorld().playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 2.0f, 1.0f);
    }

    private boolean changeBirthdayPartyLevel(LivingEntity target, int level) {
        if (!target.hasStatusEffect(ModEffects.BIRTHDAY_PARTY)) {
            return false;
        }

        int currDuration = target.getStatusEffect(ModEffects.BIRTHDAY_PARTY).getDuration();

        target.removeStatusEffect(ModEffects.BIRTHDAY_PARTY);
        return target.addStatusEffect(new StatusEffectInstance(ModEffects.BIRTHDAY_PARTY, currDuration, level, false, false), this.getEffectCause());
    }

    private void summonBirthdayGift(LivingEntity target) {
        if (!this.getWorld().isClient) {
            BirthdayGiftEntity birthdayGift = new BirthdayGiftEntity(this.getWorld());
            birthdayGift.setTarget(target);

            this.getWorld().spawnEntity(birthdayGift);
        }
    }
}
