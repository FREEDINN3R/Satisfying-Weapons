package net.freedinner.satisfying_weapons.entity.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.GiftExplosionBehavior;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class BirthdayGiftEntity extends Entity {
    private static final TrackedData<Integer> STATE = DataTracker.registerData(BirthdayGiftEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LAST_CHANGED_STATE = DataTracker.registerData(BirthdayGiftEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final String GIFT_TARGET_NBT_KEY = "gift_target";
    private static final String GIFT_DETONATOR_NBT_KEY = "gift_detonator";
    private static final String GIFT_STATE_NBT_KEY = "gift_state";
    private static final String LAST_CHANGED_STATE_NBT_KEY = "last_changed_state";
    private LivingEntity target;
    private UUID targetUUID;
    private LivingEntity detonator;
    private UUID detonatorUUID;

    public BirthdayGiftEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.setState(GiftState.EMERGING);
    }

    public BirthdayGiftEntity(World world) {
        super(ModEntities.BIRTHDAY_GIFT, world);
        this.setState(GiftState.EMERGING);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(STATE, GiftState.EMERGING.ordinal());
        this.dataTracker.startTracking(LAST_CHANGED_STATE, 0);
    }


    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            SatisfyingWeapons.LOGGER.info("C" + (this.getBlockPos().getX() + "" + this.getBlockPos().getZ()) + ":  " + this.getDetonationProgress());
        }
        else {
            SatisfyingWeapons.LOGGER.info("S" + (this.getBlockPos().getX() + "" + this.getBlockPos().getZ()) + ":  " + this.getDetonationProgress());
        }

        if (this.getWorld().isClient) {
            return;
        }

        LivingEntity currTarget = this.getTarget();

        if ((currTarget == null || !currTarget.isAlive() || currTarget.isRemoved()) && !(this.getState() == GiftState.FALLING || this.getState() == GiftState.DETONATED)) {
            this.setState(GiftState.FALLING);
            this.markStateModified();
        }

        int currStateAge = this.age - this.getLastChangedState();

        switch (this.getState()) {
            case EMERGING:
                if (currStateAge <= 10) {
                    double halfHeight = currTarget.getHeight() * 0.5;
                    double offset = halfHeight + (halfHeight + 0.5) / 10 * currStateAge;
                    Vec3d newPos = currTarget.getPos().add(0, offset, 0);

                    this.moveTo(newPos);
                }
                else {
                    this.setState(GiftState.ACTIVE);
                    this.markStateModified();
                }
                break;

            case ACTIVE:
                Vec3d giftPos = currTarget.getPos().add(0, currTarget.getHeight() + 0.5, 0);
                this.moveTo(giftPos);

                if (!currTarget.hasStatusEffect(ModEffects.BIRTHDAY_PARTY)) {
                    this.setState(GiftState.FALLING);
                    this.markStateModified();
                }
                break;

            case FALLING:
                Vec3d movement = new Vec3d(0, -0.2 * MathHelper.clamp(currStateAge, 0, 5), 0);
                this.move(MovementType.SELF, movement);

                if (this.isOnGround()) {
                    this.getWorld().createExplosion(this, this.getWorld().getDamageSources().explosion(this, null), new GiftExplosionBehavior(), this.getPos(), 1.5f, false, World.ExplosionSourceType.MOB);
                    sendExplosionParticlesPacket();

                    this.remove(RemovalReason.DISCARDED);
                }
                break;

            case DETONATED:
                if (currStateAge > 10) {
                    this.getTarget().damage(getWorld().getDamageSources().explosion(this, this.getDetonator()), 5.0f);

                    Box box = new Box(this.getTarget().getBlockPos()).expand(16, 8, 16);
                    List<Entity> surroundingEntities = getWorld().getOtherEntities(this, box)
                            .stream()
                            .filter(e -> e instanceof LivingEntity || e instanceof BirthdayGiftEntity)
                            .filter(e -> e != this.getTarget() && e != this.getDetonator())
                            .sorted((e1, e2) -> {
                                boolean b1 = e1 instanceof BirthdayGiftEntity;
                                boolean b2 = e2 instanceof BirthdayGiftEntity;
                                return b1 == b2 ? 0 : (b1 ? -1 : 1);
                            })
                            .toList();

                    int count = 5;
                    for (int i = 0; i < count; i++) {
                        ToyArrowEntity toyArrow;
                        if (this.getDetonator() != null) {
                            toyArrow = new ToyArrowEntity(this.getDetonator(), this.getWorld());
                        }
                        else {
                            toyArrow = new ToyArrowEntity(this.getPos(), this.getWorld());
                        }

                        Vec3d v;
                        if (i < surroundingEntities.size() && i < 4) {
                            Entity currEntity = surroundingEntities.get(i);
                            v = currEntity.getPos().add(0, currEntity.getHeight(), 0).subtract(this.getPos()).normalize();
                            v = v.multiply(currEntity instanceof BirthdayGiftEntity ? 2.0 : 1.5);
                        }
                        else {
                            v = MathUtils.randomPointInSphere(1.0).normalize().multiply(1.2);
                            if (v.y < 0) {
                                v = v.multiply(-1);
                            }
                        }

                        toyArrow.setPosition(this.getPos());
                        toyArrow.setVelocity(v);
                        toyArrow.canHitOwner = false;
                        toyArrow.setDamage(toyArrow.getDamage() * 0.4);
                        toyArrow.setCritical(true);
                        toyArrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;

                        this.getWorld().spawnEntity(toyArrow);
                    }

                    this.getWorld().playSound(null, this.getBlockPos(), ModSounds.BIRTHDAY_GIFT_EXPLOSION, SoundCategory.BLOCKS, 3.0f, 1.0f);
                    sendExplosionParticlesPacket();

                    this.remove(RemovalReason.DISCARDED);
                }
                break;
        }

        if (this.getState() != GiftState.EMERGING) {
            sendSmokeParticlesPacket();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient || this.isInvulnerableTo(source)) {
            return false;
        }

        if (this.getState() != GiftState.ACTIVE) {
            return false;
        }

        if (source.getSource() instanceof ToyArrowEntity toyArrow) {
            this.setState(GiftState.DETONATED);
            this.markStateModified();

            if (toyArrow.getOwner() instanceof LivingEntity livingDetonator) {
                this.setDetonator(livingDetonator);
            }

            getWorld().playSound(null, this.getBlockPos(), ModSounds.BIRTHDAY_GIFT_PRIMED, SoundCategory.MASTER, 1.0f, 1.0f);

            return true;
        }

        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return super.collidesWith(other) && other instanceof ToyArrowEntity;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (targetUUID != null) {
            nbt.putUuid(GIFT_TARGET_NBT_KEY, targetUUID);
        }

        if (detonatorUUID != null) {
            nbt.putUuid(GIFT_DETONATOR_NBT_KEY, detonatorUUID);
        }

        nbt.putInt(GIFT_STATE_NBT_KEY, this.dataTracker.get(STATE));
        nbt.putInt(LAST_CHANGED_STATE_NBT_KEY, this.dataTracker.get(LAST_CHANGED_STATE));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(GIFT_TARGET_NBT_KEY)) {
            targetUUID = nbt.getUuid(GIFT_TARGET_NBT_KEY);
        }

        if (nbt.contains(GIFT_DETONATOR_NBT_KEY)) {
            detonatorUUID = nbt.getUuid(GIFT_DETONATOR_NBT_KEY);
        }

        this.dataTracker.set(STATE, nbt.getInt(GIFT_STATE_NBT_KEY));
        this.dataTracker.set(LAST_CHANGED_STATE, nbt.getInt(LAST_CHANGED_STATE_NBT_KEY));
    }

    private void moveTo(Vec3d pos) {
        this.move(MovementType.SELF, pos.subtract(this.getPos()));
    }

    public void setTarget(LivingEntity target) {
        if (target != null) {
            this.target = target;
            this.targetUUID = target.getUuid();

            this.setPosition(target.getPos().add(0, target.getHeight() * 0.5, 0));

            this.setState(GiftState.EMERGING);
            this.markStateModified();
        }
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

    public void setDetonator(LivingEntity detonator) {
        if (detonator != null) {
            this.detonator = detonator;
            this.detonatorUUID = detonator.getUuid();
        }
    }

    public LivingEntity getDetonator() {
        if (detonator != null) {
            return detonator;
        }

        if (detonatorUUID != null && this.getWorld() instanceof ServerWorld) {
            return (LivingEntity) ((ServerWorld) this.getWorld()).getEntity(detonatorUUID);
        }

        return null;
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

    public int getDetonationProgress() {
        return (this.getState() == GiftState.DETONATED) ? this.age - this.getLastChangedState() : -1;
    }

    protected GiftState getState() {
        return GiftState.values()[this.dataTracker.get(STATE)];
    }

    private void setState(GiftState state) {
        this.dataTracker.set(STATE, state.ordinal());
        this.noClip = state == GiftState.EMERGING || state == GiftState.ACTIVE;
    }

    protected int getLastChangedState() {
        return this.dataTracker.get(LAST_CHANGED_STATE);
    }

    private void markStateModified() {
        this.dataTracker.set(LAST_CHANGED_STATE, this.age);
    }

    protected enum GiftState {
        EMERGING,
        ACTIVE,
        FALLING,
        DETONATED
    }
}
