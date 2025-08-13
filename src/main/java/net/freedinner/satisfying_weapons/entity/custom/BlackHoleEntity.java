package net.freedinner.satisfying_weapons.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.mixin.LivingEntityAccessor;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackHoleEntity extends ThrownItemEntity {
    private static final TrackedData<Integer> ACTIVE_AGE = DataTracker.registerData(BlackHoleEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Stats
    public static final float BLACK_HOLE_SPEED = 2.5f;
    public static final double BLACK_HOLE_THROW_RANGE = 14;
    public static final double BLACK_HOLE_EFFECT_RANGE = 16;
    public static final double BLACK_HOLE_EFFECT_RANGE_SQR = (int) Math.pow(BLACK_HOLE_EFFECT_RANGE, 2);

    // Timings
    public static final int BLACK_HOLE_MAX_ACTIVE_AGE = 26; // effectively 1.5 s, not sure why it's not 30
    public static final int BLACK_HOLE_GROWING_DURATION = 4;
    public static final int BLACK_HOLE_SHRINKING_DURATION = 3;

    // NBT
    private static final String SWORD_LEVEL_NBT_KEY = "black_hole_sword_level";
    private int swordLevel = 1;
    private static final String DISTANCE_TRAVELLED_NBT_KEY = "black_hole_distance_traveled";
    private double distanceTravelled = 0;
    private static final String ACTIVATION_AGE_NBT_KEY = "black_hole_activation_age";
    private int activationAge = -1;
    private static final String SHOULD_COLLECT_LOOT_NBT_KEY = "black_hole_should_collect_loot";
    private boolean shouldCollectLoot = false;

    public BlackHoleEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BlackHoleEntity(World world, LivingEntity owner, int swordLevel, boolean shouldCollectLoot) {
        super(ModEntities.BLACK_HOLE, owner, world);

        this.swordLevel = swordLevel;
        this.shouldCollectLoot = shouldCollectLoot;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(ACTIVE_AGE, -1);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            return;
        }

        // Very important, constantly refreshes activation age for both client and server to use
        if (activationAge != -1) {
            dataTracker.set(ACTIVE_AGE, this.age - activationAge);
        }

        if (!this.isActive()) {
            // If still flying, refresh velocity
            this.setVelocity(this.getVelocity().normalize().multiply(BLACK_HOLE_SPEED));

            // Activate if exceeds throw range
            distanceTravelled += BLACK_HOLE_SPEED;
            if (distanceTravelled >= BLACK_HOLE_THROW_RANGE) {
                this.activate(false);
            }
        }
        else {
            // When activated, stays in one place
            // Unfortunately it can't prevent movement fully, but at least it stops BH from gliding after being knocked back
            this.setVelocity(0, 0, 0);

            // If active and not shrinking yet
            if (this.getActiveAge() <= BLACK_HOLE_MAX_ACTIVE_AGE - BLACK_HOLE_SHRINKING_DURATION) {
                suckInEntities();

                // Visuals & SFX
                sendPullParticlesPacket();
                this.getWorld().playSound(null, this.getBlockPos(), ModSounds.BLACK_HOLE_ACTIVE, SoundCategory.MASTER, 1.6f, PitchUtils.get());
            }

            // If finished shrinking
            if (this.getActiveAge() > BLACK_HOLE_MAX_ACTIVE_AGE) {
                this.discard();
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (this.getWorld().isClient) {
            return;
        }

        if (this.isActive() || hitResult.getType() == HitResult.Type.ENTITY) {
            return;
        }

        // Activates from block collisions, backtracks to be visible
        this.activate(true);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putInt(SWORD_LEVEL_NBT_KEY, swordLevel);
        nbt.putDouble(DISTANCE_TRAVELLED_NBT_KEY, distanceTravelled);
        nbt.putInt(ACTIVATION_AGE_NBT_KEY, activationAge);
        nbt.putBoolean(SHOULD_COLLECT_LOOT_NBT_KEY, shouldCollectLoot);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        swordLevel = NbtUtils.getOrCreate(nbt, SWORD_LEVEL_NBT_KEY, 0);
        distanceTravelled = NbtUtils.getOrCreate(nbt, DISTANCE_TRAVELLED_NBT_KEY, 0.0);
        activationAge = NbtUtils.getOrCreate(nbt, ACTIVATION_AGE_NBT_KEY, 0);
        shouldCollectLoot = NbtUtils.getOrCreate(nbt, SHOULD_COLLECT_LOOT_NBT_KEY, false);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLACK_HOLE;
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    public int getActiveAge() {
        return dataTracker.get(ACTIVE_AGE);
    }

    public boolean isActive() {
        return this.getActiveAge() != -1;
    }

    private void activate(boolean backtrack) {
        if (this.isActive()) {
            return;
        }

        // If needed, go back slightly to be visible
        if (backtrack) {
            Vec3d direction = this.getVelocity().normalize();
            Vec3d newPos = this.getPos().subtract(direction.multiply(BLACK_HOLE_SPEED * 0.025));
            this.setPosition(newPos);
        }

        activationAge = this.age;
        this.setVelocity(0, 0,0);
    }

    private void suckInEntities() {
        Vec3d pos = this.getPos();
        Box box = new Box(pos, pos).expand(BLACK_HOLE_EFFECT_RANGE);

        List<Entity> affectedEntities = this.getWorld().getOtherEntities(this, box)
                .stream()
                .filter(e -> e != this.getOwner())
                .filter(e -> e.squaredDistanceTo(pos) <= BLACK_HOLE_EFFECT_RANGE_SQR) // Cuz it's a sphere, not a cube
                .filter(e -> !(e instanceof BlackHoleEntity otherBlackHole) || this.shouldCollapseWith(otherBlackHole)) // Ignore BHs not legible for collapse
                .toList();

        // For every entity in range
        for (Entity entity : affectedEntities) {
            Vec3d direction = pos.subtract(entity.getPos());
            double distance = direction.length();

            // Try to collect dropped loot
            if (shouldCollectLoot && distance < 1f) {
                this.tryPickUpLoot(entity);
            }

            double pullForce = Math.sqrt(distance) / 16;

            // Increase pull force for BHs from the same user
            if (entity instanceof BlackHoleEntity otherBlackHole && this.shouldCollapseWith(otherBlackHole)) {
                pullForce *= Math.sqrt(BLACK_HOLE_EFFECT_RANGE) / distance;

                // Collapse two BHs if they are too close
                // Extra checks so that only one explosion is produced
                if (distance < 0.5 && this.getActiveAge() > otherBlackHole.getActiveAge()) {
                    this.produceExplosion(otherBlackHole);
                    return;
                }
            }

            // Suck in entities
            Vec3d v = direction.normalize().multiply(pullForce);
            entity.addVelocity(v);
            entity.velocityModified = true;

            if (!(entity instanceof LivingEntity livingEntity)) {
                continue;
            }

            // If owner exists, set them as attacker
            if (this.getOwner() instanceof PlayerEntity owner) {
                livingEntity.setAttacking(owner);
                ((LivingEntityAccessor) livingEntity).setPlayerHitTimer(100); // setAttacking() sets it to age, idk why
            }

            // Try to inflict Entropy
            if (swordLevel >= 3) {
                livingEntity.addStatusEffect(new StatusEffectInstance(ModEffects.ENTROPY, 320, 0, false, false, true));
            }

            // Try to steal equipment
            if (swordLevel >= 4) {
                this.tryDropEquipment(livingEntity);
            }
        }
    }

    private void tryPickUpLoot(Entity entity) {
        PlayerEntity owner = (PlayerEntity) this.getOwner();
        boolean canPickUp = entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity;

        if (owner != null && canPickUp) {
            entity.onPlayerCollision(owner);
        }
    }

    private void tryDropEquipment(LivingEntity livingEntity) {
        List<EquipmentSlot> occupiedSlots = this.getOccupiedSlots(livingEntity);
        if (occupiedSlots.isEmpty()) {
            return;
        }

        ILivingEntityDataSaver dataSaver = (ILivingEntityDataSaver) livingEntity;
        if (dataSaver.sw$getDropAttemptsBH() >= 60) {
            return;
        }

        double mult = Math.cbrt(occupiedSlots.size());

        // 30% total base chance to drop at least one item, goes up to 48% with all 6 occupied slots
        if (MathUtils.takeChance(0.0059 * mult)) {
            EquipmentSlot slot = occupiedSlots.get(MathUtils.randomNumber(occupiedSlots.size()));

            // Drop the stack
            ItemStack itemStack = livingEntity.getEquippedStack(slot);
            livingEntity.equipStack(slot, ItemStack.EMPTY);
            ItemEntity droppedStack = livingEntity.dropStack(itemStack);

            // If successful, lunge the stack towards the BH
            if (droppedStack != null) {
                Vec3d direction = this.getPos().subtract(droppedStack.getPos());
                double distance = direction.length();

                double force = Math.sqrt(distance) / 8;
                Vec3d v = direction.normalize().multiply(force);

                droppedStack.addVelocity(v);
                droppedStack.velocityModified = true;
            }
        }

        dataSaver.sw$addDropAttemptsBH(1);
    }

    private List<EquipmentSlot> getOccupiedSlots(LivingEntity livingEntity) {
        ArrayList<EquipmentSlot> occupiedSlots = new ArrayList<>();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!livingEntity.getEquippedStack(slot).isEmpty()) {
                occupiedSlots.add(slot);
            }
        }

        return occupiedSlots;
    }

    private boolean shouldCollapseWith(BlackHoleEntity otherBlackHole) {
        return otherBlackHole.getOwner() == this.getOwner()
                && this.getOwner() != null
                && otherBlackHole.squaredDistanceTo(this.getPos()) < BLACK_HOLE_EFFECT_RANGE; // Not square, because BHs collapse only if very close
    }

    private void produceExplosion(BlackHoleEntity otherBlackHole) {
        if (this.getWorld().isClient || otherBlackHole == this) {
            return;
        }

        // Momentary invincibility for owner to prevent explosion damage
        if (this.getOwner() instanceof LivingEntity livingOwner) {
            livingOwner.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 4, false, false));
        }

        this.getWorld().createExplosion(this, this.getWorld().getDamageSources().explosion(this, this.getOwner()), new NonDestructiveExplosionBehavior(), this.getPos(), 6, false, World.ExplosionSourceType.MOB);

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_HURT, SoundCategory.MASTER, 1.8f, PitchUtils.get() - 0.2f);
        this.sendExplosionParticlesPacket();

        otherBlackHole.remove(RemovalReason.DISCARDED);
        this.remove(RemovalReason.DISCARDED);
    }

    private void sendPullParticlesPacket() {
        Vector3f center = this.getPos().toVector3f();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(center);
        buf.writeDouble(BLACK_HOLE_EFFECT_RANGE);

        // List of center pos, and edge pos in each cardinal direction
        List<BlockPos> blockPosList = new ArrayList<>();
        blockPosList.add(PosUtils.toBlockPos(center));
        blockPosList.add(blockPosList.get(0).north((int) BLACK_HOLE_EFFECT_RANGE));
        blockPosList.add(blockPosList.get(0).south((int) BLACK_HOLE_EFFECT_RANGE));
        blockPosList.add(blockPosList.get(0).west((int) BLACK_HOLE_EFFECT_RANGE));
        blockPosList.add(blockPosList.get(0).east((int) BLACK_HOLE_EFFECT_RANGE));
        blockPosList.add(blockPosList.get(0).down((int) BLACK_HOLE_EFFECT_RANGE));
        blockPosList.add(blockPosList.get(0).up((int) BLACK_HOLE_EFFECT_RANGE));

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
            ServerPlayNetworking.send(player, ModNetworking.BLACK_HOLE_PULL_PARTICLES_ID, buf);
        }
    }

    private void sendExplosionParticlesPacket() {
        Vec3d pos = this.getPos();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(pos.toVector3f());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.getWorld(), PosUtils.toBlockPos(pos))) {
            ServerPlayNetworking.send(player, ModNetworking.BLACK_HOLE_EXPLOSION_PARTICLES_ID, buf);
        }
    }
}
