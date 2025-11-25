package net.freedinner.satisfying_weapons.entity.custom;

import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class EnergyDischargeEntity extends ThrownItemEntity {
    private static final TrackedData<Integer> CHARGE_LEVEL = DataTracker.registerData(EnergyDischargeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final float BASE_SPEED = 2.5f;
    public static final float SPEED_INCREASE = 0.3f;
    public static final double MAX_DISTANCE_TRAVELED = 48;
    public static final List<Double> EXPLOSION_POWER = List.of(1.4, 2.0, 2.5, 3.0, 4.0);

    // NBT
    private static final String CHARGE_LEVEL_NBT_KEY = "energy_discharge_charge_level";
    private static final String DISTANCE_TRAVELED_NBT_KEY = "energy_discharge_distance_traveled";
    double distanceTraveled = 0;

    public EnergyDischargeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public EnergyDischargeEntity(World world, LivingEntity owner) {
        super(ModEntities.ENERGY_DISCHARGE, owner, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGE_LEVEL, 1);
    }

    @Override
    public void tick() {
        super.tick();

        double targetSpeed = BASE_SPEED + this.getChargeLevel() * SPEED_INCREASE;
        Vec3d targetVelocity = this.getVelocity().normalize().multiply(targetSpeed);
        this.setVelocity(targetVelocity);

        distanceTraveled += targetSpeed;
        if (distanceTraveled >= MAX_DISTANCE_TRAVELED) {
            this.onCollision(new BlockHitResult(this.getPos(), Direction.UP, this.getBlockPos(), false));
        }

        // TODO: Add energy trail
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (this.getWorld().isClient) {
            return;
        }

        if (this.getOwner() instanceof LivingEntity livingEntity && livingEntity.squaredDistanceTo(this) < 256) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 4, false, false));
        }

        double power = EXPLOSION_POWER.get(this.getChargeLevel() - 1);
        this.getWorld().createExplosion(this, this.getWorld().getDamageSources().explosion(this, this.getOwner()), new NonDestructiveExplosionBehavior(), this.getPos(), (float) power, false, World.ExplosionSourceType.MOB);

        this.discard();
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putDouble(DISTANCE_TRAVELED_NBT_KEY, distanceTraveled);
        nbt.putByte(CHARGE_LEVEL_NBT_KEY, (byte) this.getChargeLevel());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        distanceTraveled = NbtUtils.getOrCreate(nbt, DISTANCE_TRAVELED_NBT_KEY, 0);
        this.setChargeLevel(NbtUtils.getOrCreate(nbt, CHARGE_LEVEL_NBT_KEY, 1));
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ENERGY_DISCHARGE;
    }

    public void setChargeLevel(int chargeLevel) {
        this.dataTracker.set(CHARGE_LEVEL, chargeLevel);
        ScaleTypes.BASE.getScaleData(this).setScale(0.5f + chargeLevel * 0.2f);
    }

    public int getChargeLevel() {
        return this.dataTracker.get(CHARGE_LEVEL);
    }
}
