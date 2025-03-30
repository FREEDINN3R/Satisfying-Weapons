package net.freedinner.satisfying_weapons.entity.custom;

import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.custom.ActiveBlackHoleEntity;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownBlackHoleEntity extends ThrownItemEntity {
    public static final float BLACK_HOLE_SPEED = 2.5f;
    public static final double BLACK_HOLE_EFFECT_RADIUS = 14;

    private static final String DISTANCE_TRAVELLED_NBT_KEY = "distance_traveled";
    private double distanceTravelled = 0;
    private static final String ACTIVATED_VORTEX_SUMMONED_NBT_KEY = "activated_vortex_summoned";
    private boolean activatedVortexSummoned = false;

    public ThrownBlackHoleEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrownBlackHoleEntity(World world, LivingEntity owner) {
        super(ModEntities.THROWN_BLACK_HOLE, owner, world);
    }


    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            return;
        }

        this.setVelocity(this.getVelocity().normalize().multiply(BLACK_HOLE_SPEED));
        distanceTravelled += BLACK_HOLE_SPEED;
        if (distanceTravelled >= BLACK_HOLE_EFFECT_RADIUS) {
            activateVortex(this.getPos());
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (this.getWorld().isClient) {
            return;
        }

        Vec3d direction = this.getVelocity().normalize();
        Vec3d vortexPos = this.getPos().subtract(direction.multiply(0.2));
        activateVortex(vortexPos);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putDouble(DISTANCE_TRAVELLED_NBT_KEY, distanceTravelled);
        nbt.putBoolean(ACTIVATED_VORTEX_SUMMONED_NBT_KEY, activatedVortexSummoned);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(DISTANCE_TRAVELLED_NBT_KEY)) {
            distanceTravelled = nbt.getDouble(DISTANCE_TRAVELLED_NBT_KEY);
        }
        if (nbt.contains(ACTIVATED_VORTEX_SUMMONED_NBT_KEY)) {
            activatedVortexSummoned = nbt.getBoolean(ACTIVATED_VORTEX_SUMMONED_NBT_KEY);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLACK_HOLE;
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    private void activateVortex(Vec3d pos) {
        if (activatedVortexSummoned) {
            return;
        }
        activatedVortexSummoned = true;

        ActiveBlackHoleEntity activePocketVortex = new ActiveBlackHoleEntity(this.getWorld(), pos);
        if (this.getOwner() != null) {
            activePocketVortex.setOwner(this.getOwner());
        }
        this.getWorld().spawnEntity(activePocketVortex);

        this.discard();
    }
}
