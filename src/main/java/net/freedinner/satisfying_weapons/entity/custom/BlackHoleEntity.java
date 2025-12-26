package net.freedinner.satisfying_weapons.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.config.ModConfigs;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.mixin.LivingEntityAccessor;
import net.freedinner.satisfying_weapons.mixin.ProjectileEntityAccessor;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.freedinner.satisfying_weapons.util.SoundUtils;
import net.freedinner.satisfying_weapons.util.data.ILivingEntityDataSaver;
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
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlackHoleEntity extends ThrownItemEntity {
    private static final TrackedData<Integer> ACTIVE_AGE = DataTracker.registerData(BlackHoleEntity.class, TrackedDataHandlerRegistry.INTEGER);

    // Stats
    public static final float BASE_SPEED = 2.5f;
    public static final double THROW_RANGE = 14;
    public static final double PULL_RANGE = 16;
    public static final double PULL_RANGE_SQR = (int) Math.pow(PULL_RANGE, 2);

    // Timings
    public static final int MAX_TOTAL_AGE = 26; // effectively 1.5 s, not sure why it's not 30
    public static final int GROWING_DURATION = 4;
    public static final int SHRINKING_DURATION = 3;

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
            this.setVelocity(this.getVelocity().normalize().multiply(BASE_SPEED));

            // Activate if exceeds throw range
            distanceTravelled += BASE_SPEED;
            if (distanceTravelled >= THROW_RANGE) {
                this.activate(false);
            }
        }
        else {
            // When activated, stays in one place
            // Unfortunately it can't prevent movement fully, but at least it stops BH from gliding after being knocked back
            this.setVelocity(0, 0, 0);

            // If active and not shrinking yet
            if (this.getActiveAge() <= MAX_TOTAL_AGE - SHRINKING_DURATION) {
                attractEntities();

                // Visuals & SFX
                sendPullParticlesPacket();
                this.getWorld().playSound(null, this.getBlockPos(), ModSounds.BLACK_HOLE_ACTIVE, SoundCategory.MASTER, 1.6f, SoundUtils.getPitch());
            }

            // If finished shrinking
            if (this.getActiveAge() > MAX_TOTAL_AGE) {
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
            Vec3d newPos = this.getPos().subtract(direction.multiply(BASE_SPEED * 0.025));
            this.setPosition(newPos);
        }

        activationAge = this.age;
        this.setVelocity(0, 0,0);
    }

    private void attractEntities() {
        Vec3d pos = this.getPos();
        Box box = new Box(pos, pos).expand(PULL_RANGE);

        List<Entity> affectedEntities = this.getWorld().getOtherEntities(this, box)
                .stream()
                .filter(e -> e != this.getOwner())
                .filter(e -> e.squaredDistanceTo(pos) <= PULL_RANGE_SQR) // Cuz it's a sphere, not a cube
                .filter(e -> !ModConfigs.BLACK_HOLE_IGNORED_ENTITIES.contains(Registries.ENTITY_TYPE.getId(e.getType()).toString()))
                .filter(e -> !this.shouldIgnorePet(e)) // As dictated by config
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
                pullForce *= Math.sqrt(PULL_RANGE) / distance;

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

    private void tryPickUpLoot(Entity lootEntity) {
        PlayerEntity owner = (PlayerEntity) this.getOwner();
        boolean canPickUp = lootEntity instanceof ItemEntity || lootEntity instanceof ExperienceOrbEntity;

        if (owner != null && canPickUp) {
            lootEntity.onPlayerCollision(owner);

            // Reset EXP pickup cooldown because it's annoying
            if (lootEntity instanceof ExperienceOrbEntity) {
                owner.experiencePickUpDelay = 0;
            }
        }
    }

    private void tryDropEquipment(LivingEntity livingEntity) {
        List<EquipmentSlot> occupiedSlots = this.getOccupiedSlots(livingEntity);
        if (occupiedSlots.isEmpty()) {
            return;
        }

        ILivingEntityDataSaver dataSaver = (ILivingEntityDataSaver) livingEntity;
        int dropAttempts = dataSaver.sw$getDropAttemptsBH();
        if (dropAttempts >= 60) {
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

        dataSaver.sw$setDropAttemptsBH(dropAttempts + 1);
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

    private boolean shouldIgnorePet(Entity entity) {
        if (ModConfigs.BLACK_HOLE_IGNORE_PETS.equals("no")) {
            return false;
        }

        if (!(entity instanceof Tameable tameableEntity) || tameableEntity.getOwnerUuid() == null) {
            return false;
        }

        if (ModConfigs.BLACK_HOLE_IGNORE_PETS.equals("yes")) {
            return true;
        }

        // From this point, we assume IGNORE_PETS == "only_your_own"

        return tameableEntity.getOwnerUuid() == ((ProjectileEntityAccessor) this).getOwnerUuid(); // Weird comparison because .getOwner() may return null if the owner is offline
    }

    private boolean shouldCollapseWith(BlackHoleEntity otherBlackHole) {
        return otherBlackHole.getOwner() == this.getOwner()
                && this.getOwner() != null
                && otherBlackHole.squaredDistanceTo(this.getPos()) < PULL_RANGE; // Not square, because BHs collapse only if very close
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

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_HURT, SoundCategory.MASTER, 1.8f, SoundUtils.getPitch() - 0.2f);
        this.sendExplosionParticlesPacket();

        otherBlackHole.remove(RemovalReason.DISCARDED);
        this.remove(RemovalReason.DISCARDED);
    }

    private void sendPullParticlesPacket() {
        World world = this.getWorld();
        Vec3d centerPos = this.getPos();
        int range = (int) PULL_RANGE;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(centerPos.toVector3f());
        buf.writeDouble(PULL_RANGE);

        // List of center and edge positions to be used for tracking
        BlockPos origin = PosUtils.toBlockPos(centerPos);
        List<BlockPos> trackingPosList = List.of(
                origin,
                origin.north(range),
                origin.south(range),
                origin.west(range),
                origin.east(range),
                origin.down(range),
                origin.up(range)
        );

        // All players who are tracking at least one pos
        Set<ServerPlayerEntity> trackingPlayers = new HashSet<>();
        for (BlockPos blockPos : trackingPosList) {
            trackingPlayers.addAll(PosUtils.getPlayersTracking(blockPos, world));
        }

        // Sending the packet here
        for (ServerPlayerEntity player : trackingPlayers) {
            ServerPlayNetworking.send(player, ModNetworking.BLACK_HOLE_PULL_PARTICLES_ID, buf);
        }
    }

    private void sendExplosionParticlesPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(this.getPos().toVector3f());

        for (ServerPlayerEntity player : PosUtils.getPlayersTracking(this.getPos(), this.getWorld())) {
            ServerPlayNetworking.send(player, ModNetworking.BLACK_HOLE_EXPLOSION_PARTICLES_ID, buf);
        }
    }
}
