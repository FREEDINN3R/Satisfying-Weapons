package net.freedinner.satisfying_weapons.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.custom.ActiveBlackHoleEntity;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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

    /*private void setSizeForAge() {
        double scale;

        if (age <= BLACK_HOLE_MAX_GROWING_AGE) {
            scale = 1.0 + 0.5 * age / BLACK_HOLE_MAX_GROWING_AGE;
        }
        else if (age <= BLACK_HOLE_MAX_ACTIVE_AGE) {
            scale = 1.0 + (((age - BLACK_HOLE_MAX_GROWING_AGE) % 2 == 1) ? 0.3 : 0.5);
        }
        else {
            scale = 1.5 * (BLACK_HOLE_MAX_TOTAL_AGE - age) / (BLACK_HOLE_MAX_TOTAL_AGE - BLACK_HOLE_MAX_ACTIVE_AGE);
        }

        ScaleTypes.BASE.getScaleData(this).setScale((float) scale);
    }*/

    private void applySuctionToEntities() {
        Vec3d pos = this.getPos();
        Box box = new Box(pos, pos).expand(BLACK_HOLE_EFFECT_RADIUS);

        List<Entity> affectedEntities = this.getWorld().getOtherEntities(this.getOwner(), box)
                .stream()
                .filter(e -> e.squaredDistanceTo(pos) <= Math.pow(BLACK_HOLE_EFFECT_RADIUS, 2))
                .toList();

        for (Entity entity : affectedEntities) {
            Vec3d direction = entity.getPos().subtract(pos);
            double distanceSqrt = Math.sqrt(direction.length());

            double force = distanceSqrt / 15;
            Vec3d v = direction.normalize().multiply(-force);
            entity.addVelocity(v);
            entity.velocityModified = true;
        }
    }

    private void sendParticlesPacket() {
        Vector3f center = this.getPos().toVector3f();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(center);
        buf.writeDouble(BLACK_HOLE_EFFECT_RADIUS);

        BlockPos blockPos1 = new BlockPos(Math.round(center.x), Math.round(center.y), Math.round(center.z));
        BlockPos blockPos2 = blockPos1.north((int) BLACK_HOLE_EFFECT_RADIUS);
        BlockPos blockPos3 = blockPos1.south((int) BLACK_HOLE_EFFECT_RADIUS);
        BlockPos blockPos4 = blockPos1.west((int) BLACK_HOLE_EFFECT_RADIUS);
        BlockPos blockPos5 = blockPos1.east((int) BLACK_HOLE_EFFECT_RADIUS);

        ServerWorld world = (ServerWorld) this.getWorld();

        Collection<ServerPlayerEntity> players1 = PlayerLookup.tracking(world, blockPos1);
        Collection<ServerPlayerEntity> players2 = PlayerLookup.tracking(world, blockPos2);
        Collection<ServerPlayerEntity> players3 = PlayerLookup.tracking(world, blockPos3);
        Collection<ServerPlayerEntity> players4 = PlayerLookup.tracking(world, blockPos4);
        Collection<ServerPlayerEntity> players5 = PlayerLookup.tracking(world, blockPos5);

        List<ServerPlayerEntity> players = Stream.of(players1, players2, players3, players4, players5)
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, ModNetworking.BLACK_HOLE_PARTICLES_ID, buf);
        }
    }
}
