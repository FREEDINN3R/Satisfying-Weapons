package net.freedinner.satisfying_weapons.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.misc.ToyArrowEntityData;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.freedinner.satisfying_weapons.util.SoundUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BirthdayGiftEntity extends Entity {
    private static final TrackedData<Integer> STATE = DataTracker.registerData(BirthdayGiftEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String STATE_NBT_KEY = "gift_state";

    private static final TrackedData<Integer> STATE_AGE = DataTracker.registerData(BirthdayGiftEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String STATE_AGE_NBT_KEY = "gift_state_age";

    private static final String GIFT_TARGET_NBT_KEY = "gift_target";
    private LivingEntity target;
    private UUID targetUUID;
    private static final String GIFT_OWNER_NBT_KEY = "gift_owner";
    private UUID ownerUUID;

    private ToyArrowEntityData detonatorArrowData;

    public BirthdayGiftEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.setState(GiftState.EMERGING);
    }

    public BirthdayGiftEntity(World world, @NotNull LivingEntity target, LivingEntity owner) {
        super(ModEntities.BIRTHDAY_GIFT, world);

        this.setState(GiftState.EMERGING);
        this.setTarget(target);
        this.setOwner(owner);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(STATE, GiftState.EMERGING.ordinal());
        this.dataTracker.startTracking(STATE_AGE, 0);
    }


    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            return;
        }

        // Increment state age by 1
        this.updateStateAge();

        // Get target, after world reload works only on server
        LivingEntity currTarget = this.getTarget();

        // If missing target or it's dead, and not falling / detonating already, fall down
        if ((currTarget == null || !currTarget.isAlive()) && !(this.getState() == GiftState.FALLING || this.getState() == GiftState.DETONATED)) {
            this.setState(GiftState.FALLING);
        }

        // Custom logic for every state
        switch (this.getState()) {
            case EMERGING:
                assert currTarget != null; // If it were, the gift would already fall

                if (this.getStateAge() <= 10) {
                    double halfHeight = 0.5 * currTarget.getHeight();

                    // Linearly rise 0.5 blocks above the target's head
                    double offset = halfHeight + (halfHeight + 0.5) / 10 * this.getStateAge();
                    Vec3d newPos = currTarget.getPos().add(0, offset, 0);

                    this.moveTo(newPos);
                }
                else {
                    // When reaching the top, becomes active
                    this.setState(GiftState.ACTIVE);
                }
                break;

            case ACTIVE:
                assert currTarget != null; // If it were, the gift would already fall

                // Hover 0.5 blocks above the target's head
                Vec3d giftPos = currTarget.getPos().add(0, currTarget.getHeight() + 0.5, 0);
                this.moveTo(giftPos);

                // When birthday party ends, falls down
                if (!currTarget.hasStatusEffect(ModEffects.BIRTHDAY_PARTY)) {
                    this.setState(GiftState.FALLING);
                }
                break;

            case FALLING:
                // Fall down, up to 0.8 block per tick
                double fallVelocity = 0.2 * Math.min(this.getStateAge(), 5);
                this.moveTo(this.getPos().subtract(0, fallVelocity, 0));

                if (this.isOnGround()) {
                    // Momentary invincibility for owner to prevent explosion damage
                    LivingEntity giftOwner = this.getOwner();
                    if (giftOwner != null) {
                        giftOwner.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 4, false, false));
                    }

                    // Create a non-destructive explosion
                    this.getWorld().createExplosion(this, this.getWorld().getDamageSources().explosion(this, this.getOwner()), new NonDestructiveExplosionBehavior(), this.getPos(), 1.5f, false, World.ExplosionSourceType.MOB);
                    sendExplosionParticlesPacket();

                    this.remove(RemovalReason.DISCARDED);
                }
                break;

            case DETONATED:
                if (this.getStateAge() > 10) {
                    Entity detonator;

                    // After 10 ticks, apply explosive damage to entity
                    if (detonatorArrowData != null) {
                        detonator = detonatorArrowData.getOwner(this.getWorld());
                        float damage = 2 * (float) detonatorArrowData.damage;

                        // Apply explosive damage equal to 2 * arrow damage
                        this.getTarget().damage(this.getWorld().getDamageSources().explosion(this, detonator), damage);
                    }
                    else {
                        detonator = null;

                        // Apply explosive damage equal to 10 HP
                        this.getTarget().damage(this.getWorld().getDamageSources().explosion(this, null), 10);
                    }

                    // Find all living entities in an area equal to Birthday Party effect area
                    Box box = new Box(this.getTarget().getBlockPos()).expand(16, 8, 16);
                    List<Entity> surroundingEntities = getWorld().getOtherEntities(this, box)
                            .stream()
                            .filter(e -> e instanceof LivingEntity || e instanceof BirthdayGiftEntity)
                            .filter(e -> e != this.getTarget() && e != detonator)
                            .sorted((e1, e2) -> {
                                // Other Birthday Gifts come first
                                boolean b1 = e1 instanceof BirthdayGiftEntity;
                                boolean b2 = e2 instanceof BirthdayGiftEntity;
                                return b1 == b2 ? 0 : (b1 ? -1 : 1);
                            })
                            .toList();

                    // Summon 5 identical Toy Arrows
                    for (int i = 0; i < 5; i++) {
                        ToyArrowEntity toyArrow = new ToyArrowEntity(this.getPos(), this.getWorld());

                        // If detonator arrow is known, copy its data to this arrow
                        if (detonatorArrowData != null) {
                            detonatorArrowData.pasteDataTo(toyArrow);
                        }
                        else {
                            // If not, set level to 5, leave everything else unchanged
                            toyArrow.setToyBowLevel(5);
                        }

                        // Calculate velocity
                        Vec3d v;
                        if (i < surroundingEntities.size()) {
                            // If there are other entities around, aim at them
                            Entity otherEntity = surroundingEntities.get(i);
                            v = otherEntity.getPos().add(0, otherEntity.getHeight(), 0).subtract(this.getPos()).normalize();
                            v = v.multiply(otherEntity instanceof BirthdayGiftEntity ? 2.0 : 1.5);
                        }
                        else {
                            // If not, pick a random direction
                            v = MathUtils.randomPointInSphere(1.0).normalize().multiply(1.2);
                            if (v.y < 0) {
                                v = v.multiply(-1);
                            }
                        }

                        toyArrow.setVelocity(v);

                        toyArrow.setCritical(true);
                        toyArrow.canHitOwner = false;
                        toyArrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

                        this.getWorld().spawnEntity(toyArrow);
                    }

                    // Visuals & SFX
                    this.getWorld().playSound(null, this.getBlockPos(), ModSounds.BIRTHDAY_GIFT_EXPLOSION, SoundCategory.BLOCKS, 3.0f, SoundUtils.getPitch());
                    sendExplosionParticlesPacket();

                    this.remove(RemovalReason.DISCARDED);
                }
                break;
        }

        // If already emerged, emit smoke
        if (this.getState() != GiftState.EMERGING) {
            sendSmokeParticlesPacket();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // This method handles gift detonation by Toy Arrows

        if (this.getWorld().isClient || this.isInvulnerableTo(source)) {
            return false;
        }

        // Only active gifts can be detonated
        if (this.getState() != GiftState.ACTIVE) {
            return false;
        }

        // If Toy Arrow of level 5 hits this gift
        if (source.getSource() instanceof ToyArrowEntity toyArrow && toyArrow.getToyBowLevel() >= 5) {
            this.setState(GiftState.DETONATED);

            // Save data about detonator arrow
            this.detonatorArrowData = ToyArrowEntityData.copyDataFrom(toyArrow);

            // Visuals & SFX
            getWorld().playSound(null, this.getBlockPos(), ModSounds.BIRTHDAY_GIFT_PRIMED, SoundCategory.MASTER, 1.0f, 1.0f);

            return true;
        }

        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        // Only level 5 Toy Arrows can collide with the gift
        return false;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Both can be null if the gift is summoned through commands, leading to a crash
        NbtUtils.putIfExists(nbt, GIFT_TARGET_NBT_KEY, targetUUID);
        NbtUtils.putIfExists(nbt, GIFT_OWNER_NBT_KEY, ownerUUID);

        nbt.putInt(STATE_NBT_KEY, this.getState().ordinal());
        nbt.putInt(STATE_AGE_NBT_KEY, this.getStateAge());

        if (detonatorArrowData != null) {
            detonatorArrowData.saveDataTo(nbt);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        targetUUID = NbtUtils.getOrCreate(nbt, GIFT_TARGET_NBT_KEY, null);
        ownerUUID = NbtUtils.getOrCreate(nbt, GIFT_OWNER_NBT_KEY, null);

        int stateId = NbtUtils.getOrCreate(nbt, STATE_NBT_KEY, 0);
        this.setState(stateId);

        int stateAge = NbtUtils.getOrCreate(nbt, STATE_AGE_NBT_KEY, 0);
        this.setStateAge(stateAge);

        detonatorArrowData = ToyArrowEntityData.loadDataFrom(nbt);
    }

    private void moveTo(Vec3d pos) {
        Vec3d movement = pos.subtract(this.getPos());
        this.move(MovementType.SELF, movement);
    }

    public void setTarget(@NotNull LivingEntity target) {
        this.target = target;
        this.targetUUID = target.getUuid();

        this.setPosition(target.getPos().add(0, target.getHeight() * 0.5, 0));

        this.setState(GiftState.EMERGING);
    }

    public LivingEntity getTarget() {
        if (target != null) {
            return target;
        }

        if (targetUUID != null && this.getWorld() instanceof ServerWorld) {
            return (LivingEntity) ((ServerWorld) this.getWorld()).getEntity(targetUUID);
        }

        return null;
    }

    public void setOwner(LivingEntity owner) {
        this.ownerUUID = owner.getUuid();
    }

    public LivingEntity getOwner() {
        if (ownerUUID != null && this.getWorld() instanceof ServerWorld) {
            return (LivingEntity) ((ServerWorld) this.getWorld()).getEntity(ownerUUID);
        }

        return null;
    }

    public boolean isActive() {
        return this.getState() == GiftState.ACTIVE;
    }

    public boolean isDetonated() {
        return this.getState() == GiftState.DETONATED;
    }

    protected GiftState getState() {
        return GiftState.values()[this.dataTracker.get(STATE)];
    }

    protected void setState(GiftState state) {
        this.dataTracker.set(STATE, state.ordinal());
        this.resetStateAge();

        this.noClip = state != GiftState.FALLING;
    }

    protected void setState(int stateOrdinal) {
        this.setState(GiftState.values()[stateOrdinal]);
    }

    public int getStateAge() {
        return this.dataTracker.get(STATE_AGE);
    }

    protected void setStateAge(int stateAge) {
        this.dataTracker.set(STATE_AGE, stateAge);
    }

    protected void updateStateAge() {
        this.dataTracker.set(STATE_AGE, this.dataTracker.get(STATE_AGE) + 1);
    }

    protected void resetStateAge() {
        this.dataTracker.set(STATE_AGE, 0);
    }

    private void sendSmokeParticlesPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(this.getPos().add(0, this.getHeight() / 2, 0).toVector3f());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.GIFT_SMOKE_PARTICLES_ID, buf);
        }
    }

    private void sendExplosionParticlesPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(this.getPos().add(0, this.getHeight() / 2, 0).toVector3f());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos())) {
            ServerPlayNetworking.send(player, ModNetworking.GIFT_EXPLOSION_PARTICLES_ID, buf);
        }
    }

    protected enum GiftState {
        EMERGING,
        ACTIVE,
        FALLING,
        DETONATED
    }
}
