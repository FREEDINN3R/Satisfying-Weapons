package net.freedinner.satisfying_weapons.entity.custom;

import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.freedinner.satisfying_weapons.item.ModItems;
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

public class EnergyDischargeEntity extends ThrownItemEntity {
    private static final TrackedData<Integer> CHARGE_LEVEL = DataTracker.registerData(EnergyDischargeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final double THROW_RANGE = 48;

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
    public void tick() {
        super.tick();

        double actualSpeed = 2.75 + this.getChargeLevel() * 0.25;
        Vec3d actualVelocity = this.getVelocity().normalize().multiply(actualSpeed);
        this.setVelocity(actualVelocity);

        distanceTraveled += actualSpeed;
        if (distanceTraveled >= THROW_RANGE) {
            this.onCollision(new BlockHitResult(this.getPos(), Direction.UP, this.getBlockPos(), false));
        }

        // Something something energy trail behind the discharge
        /*if (this.getWorld().isClient) {
            double baseOffsetY = 0.07 + 0.02 * this.getChargeLevel();
            double baseSize = 0.13 + 0.05 * this.getChargeLevel();

            Vec3d trailDir = this.getVelocity().normalize().multiply(-1);
            double trailLength = this.getVelocity().length();

            double currTrailLength = 0;

            while (currTrailLength / trailLength < 0.9) {
                Vec3d particlePos = this.getPos().add(trailDir.multiply(currTrailLength));
                double currSize = baseSize * (1 - currTrailLength / trailLength);
                currSize = Math.round(currSize * 1000) / 1000.0;

                world.addParticle(ModParticles.ENERGY_TRAIL, particlePos.x, particlePos.y + baseOffsetY, particlePos.z, (this.getVelocity().x + 5) + Math.round(currSize * 10000), this.getVelocity().y, this.getVelocity().z);
                currTrailLength += currSize * 0.7;
            }
        }*/
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (this.getWorld().isClient) {
            return;
        }

        if (this.getOwner() instanceof LivingEntity livingEntity && livingEntity.distanceTo(this) < 16) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 4, false, false));
        }

        this.getWorld().createExplosion(this, this.getWorld().getDamageSources().explosion(this, this.getOwner()), new NonDestructiveExplosionBehavior(), this.getPos(), 0.9f + 0.5f * this.getChargeLevel(), false, World.ExplosionSourceType.MOB);

        /*double shockwaveSpeed = 0.5 + 0.4 * chargeLevel;

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.world, this.getBlockPos())) {
            double distance = this.getPos().multiply(1, 0, 1).distanceTo(player.getPos().multiply(1, 0, 1));
            if (distance > shockwaveSpeed * 8) {
                continue;
            }

            int duration = 20 + (int) Math.ceil(distance / shockwaveSpeed);
            player.addStatusEffect(new StatusEffectInstance(ModEffects.CAMERA_SHAKE, duration, chargeLevel - 1, false, false));
        }

        sendExplosionParticlesPacket();*/

        this.discard();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGE_LEVEL, 1);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putDouble(DISTANCE_TRAVELED_NBT_KEY, distanceTraveled);
        nbt.putByte(CHARGE_LEVEL_NBT_KEY, (byte) this.getChargeLevel());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(DISTANCE_TRAVELED_NBT_KEY)) {
            distanceTraveled = nbt.getDouble(DISTANCE_TRAVELED_NBT_KEY);
        }
        if (nbt.contains(CHARGE_LEVEL_NBT_KEY)) {
            this.setChargeLevel(nbt.getByte(CHARGE_LEVEL_NBT_KEY));
        }
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

    /*private void sendExplosionParticlesPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(this.getPos().toVector3f());
        buf.writeInt(this.getChargeLevel());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.world, this.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworkingPackets.REALISTIC_EXPLOSION_PARTICLES_ID, buf);
        }
    }*/
}
