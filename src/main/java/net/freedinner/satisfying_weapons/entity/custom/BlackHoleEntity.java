package net.freedinner.satisfying_weapons.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class BlackHoleEntity extends ThrownItemEntity {
    public static final float BLACK_HOLE_SPEED = 2.5f;
    public static final double BLACK_HOLE_THROW_RANGE = 14;
    public static final int BLACK_HOLE_MAX_ACTIVE_AGE = 25;
    public static final int BLACK_HOLE_GROWING_DURATION = 3;
    public static final int BLACK_HOLE_SHRINKING_DURATION = 2;

    private static final String DISTANCE_TRAVELLED_NBT_KEY = "black_hole_distance_traveled";
    private double distanceTravelled = 0;
    private static final String ACTIVATION_AGE_NBT_KEY = "black_hole_activation_age";
    private int activationAge = -1;

    public BlackHoleEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BlackHoleEntity(World world, LivingEntity owner) {
        super(ModEntities.BLACK_HOLE, owner, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            return;
        }
        if (!this.isActive()) {
            // If still flying, update velocity
            this.setVelocity(this.getVelocity().normalize().multiply(BLACK_HOLE_SPEED));

            // Activate if exceeds throw range
            distanceTravelled += BLACK_HOLE_SPEED;
            if (distanceTravelled >= BLACK_HOLE_THROW_RANGE) {
                this.activate(false);
            }
        }
        else {
            this.setVelocity(0, 0, 0);

            // If active and not shrinking yet
            if (this.getActiveAge() <= BLACK_HOLE_MAX_ACTIVE_AGE - BLACK_HOLE_SHRINKING_DURATION) {
                suckInEntities();

                // Visuals & SFX
                sendParticlesPacket();
                this.getWorld().playSound(null, this.getBlockPos(), ModSounds.BLACK_HOLE_ACTIVATES, SoundCategory.MASTER, 1.2f, PitchUtils.get());
            }

            // If finished shrinking
            if (age > BLACK_HOLE_MAX_ACTIVE_AGE) {
                this.discard();
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (this.getWorld().isClient) {
            return;
        }

        if (this.isActive()) {
            return;
        }

        // Activates from any collision, backtracks a bit to be visible
        this.activate(true);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putDouble(DISTANCE_TRAVELLED_NBT_KEY, distanceTravelled);
        nbt.putInt(ACTIVATION_AGE_NBT_KEY, activationAge);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(DISTANCE_TRAVELLED_NBT_KEY)) {
            distanceTravelled = nbt.getDouble(DISTANCE_TRAVELLED_NBT_KEY);
        }
        if (nbt.contains(ACTIVATION_AGE_NBT_KEY)) {
            activationAge = nbt.getInt(ACTIVATION_AGE_NBT_KEY);
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

    public boolean isActive() {
        return activationAge != -1;
    }

    public int getActiveAge() {
        if (!this.isActive()) {
            return 0;
        }

        return age - activationAge;
    }

    private void activate(boolean backtrack) {
        if (this.isActive()) {
            return;
        }

        // If needed, go back one tick
        if (backtrack) {
            Vec3d direction = this.getVelocity().normalize();
            Vec3d newPos = this.getPos().subtract(direction.multiply(BLACK_HOLE_SPEED * 0.05));
            this.setPosition(newPos);
        }

        activationAge = this.age;
        this.setVelocity(0, 0,0);
    }

    private void suckInEntities() {
        Vec3d pos = this.getPos();
        Box box = new Box(pos, pos).expand(BLACK_HOLE_THROW_RANGE + 1); // +1 cuz otherwise it's annoying

        List<Entity> affectedEntities = this.getWorld().getOtherEntities(this.getOwner(), box)
                .stream()
                .filter(e -> e.squaredDistanceTo(pos) <= Math.pow(BLACK_HOLE_THROW_RANGE + 1, 2))
                .toList();

        for (Entity entity : affectedEntities) {
            Vec3d direction = pos.subtract(entity.getPos());
            double distance = direction.length();

            // Suck in entities, depending on their distance from the black hole
            double force = Math.sqrt(distance) / 16;
            Vec3d v = direction.normalize().multiply(force);
            entity.addVelocity(v);
            entity.velocityModified = true;
        }
    }

    private void sendParticlesPacket() {
        Vector3f center = this.getPos().toVector3f();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(center);
        buf.writeDouble(BLACK_HOLE_THROW_RANGE + 1); // +1 cuz it's for the lines and debris

        // List of center pos, and edge pos in each cardinal direction
        List<BlockPos> blockPosList = new ArrayList<>();
        blockPosList.add(PosUtils.toBlockPos(center));
        blockPosList.add(blockPosList.get(0).north((int) BLACK_HOLE_THROW_RANGE)); // No +1 cuz it makes no difference for tracking
        blockPosList.add(blockPosList.get(0).south((int) BLACK_HOLE_THROW_RANGE));
        blockPosList.add(blockPosList.get(0).west((int) BLACK_HOLE_THROW_RANGE));
        blockPosList.add(blockPosList.get(0).east((int) BLACK_HOLE_THROW_RANGE));
        blockPosList.add(blockPosList.get(0).down((int) BLACK_HOLE_THROW_RANGE));
        blockPosList.add(blockPosList.get(0).up((int) BLACK_HOLE_THROW_RANGE));

        ServerWorld world = (ServerWorld) this.getWorld();
        Collection<ServerPlayerEntity> trackingPlayers = new ArrayList<>();

        // All players who are tracking at least one pos
        for (BlockPos blockPos : blockPosList) {
            trackingPlayers.addAll(PlayerLookup.tracking(world, blockPos));
        }

        // Remove duplicates
        trackingPlayers = trackingPlayers.stream()
                .distinct()
                .toList();

        // Send particle packet
        for (ServerPlayerEntity player : trackingPlayers) {
            ServerPlayNetworking.send(player, ModNetworking.BLACK_HOLE_PARTICLES_ID, buf);
        }
    }
}
