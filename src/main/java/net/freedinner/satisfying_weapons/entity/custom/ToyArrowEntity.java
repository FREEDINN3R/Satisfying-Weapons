package net.freedinner.satisfying_weapons.entity.custom;

import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ToyArrowEntity extends PersistentProjectileEntity {
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
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        // If no Birthday Party, apply Birthday Party
        if (target.isAlive() && !target.hasStatusEffect(ModEffects.BIRTHDAY_PARTY)) {
            StatusEffectInstance birthdayPartyEffect = new StatusEffectInstance(ModEffects.BIRTHDAY_PARTY, 200, 0, false, false);
            target.addStatusEffect(birthdayPartyEffect, this.getEffectCause());
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
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(CAN_HIT_OWNER_NBT_KEY)) {
            canHitOwner = nbt.getBoolean(CAN_HIT_OWNER_NBT_KEY);
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.ARROW);
    }
}
