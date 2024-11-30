package net.freedinner.satisfying_weapons.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.UUID;

public class ToyArrowEntityData {
    public int toyBowLevel = 5;
    public double damage = 5;
    public int punch = 0;
    public boolean onFire = false;
    private UUID ownerUUID = null;

    private static final String TOY_ARROW_LEVEL_NBT_KEY = "toy_arrow_data_level";
    private static final String TOY_ARROW_DAMAGE_NBT_KEY = "toy_arrow_damage_level";
    private static final String TOY_ARROW_PUNCH_NBT_KEY = "toy_arrow_punch_level";
    private static final String TOY_ARROW_ON_FIRE_NBT_KEY = "toy_arrow_on_fire";
    private static final String TOY_ARROW_OWNER_UUID_NBT_KEY = "toy_arrow_owner_uuid";

    private ToyArrowEntityData() {
    }

    private ToyArrowEntityData(ToyArrowEntity toyArrow) {
        this.toyBowLevel = toyArrow.getToyBowLevel();
        this.damage = toyArrow.getDamage();
        this.punch = toyArrow.getPunch();
        this.onFire = toyArrow.isOnFire();

        if (toyArrow.getOwner() != null) {
            this.ownerUUID = toyArrow.getOwner().getUuid();
        }
    }

    public Entity getOwner(World world) {
        if (ownerUUID != null && world instanceof ServerWorld serverWorld) {
            return serverWorld.getEntity(ownerUUID);
        }

        return null;
    }

    public static ToyArrowEntityData copyDataFrom(ToyArrowEntity toyArrow) {
        return new ToyArrowEntityData(toyArrow);
    }

    public void pasteDataTo(ToyArrowEntity toyArrow) {
        toyArrow.setToyBowLevel(this.toyBowLevel);
        toyArrow.setDamage(this.damage);
        toyArrow.setPunch(this.punch);

        if (this.onFire) {
            toyArrow.setOnFireFor(100);
        }

        if (ownerUUID != null && toyArrow.getWorld() instanceof ServerWorld serverWorld) {
            toyArrow.setOwner(serverWorld.getEntity(ownerUUID));
        }
    }

    public static ToyArrowEntityData loadDataFrom(NbtCompound nbt) {
        ToyArrowEntityData data = new ToyArrowEntityData();
        boolean hasAnyData = false;

        if (hasAnyData |= nbt.contains(TOY_ARROW_LEVEL_NBT_KEY)) {
            data.toyBowLevel = nbt.getInt(TOY_ARROW_LEVEL_NBT_KEY);
        }

        if (hasAnyData |= nbt.contains(TOY_ARROW_DAMAGE_NBT_KEY)) {
            data.damage = nbt.getDouble(TOY_ARROW_DAMAGE_NBT_KEY);
        }

        if (hasAnyData |= nbt.contains(TOY_ARROW_PUNCH_NBT_KEY)) {
            data.punch = nbt.getInt(TOY_ARROW_PUNCH_NBT_KEY);
        }

        if (hasAnyData |= nbt.contains(TOY_ARROW_ON_FIRE_NBT_KEY)) {
            data.onFire = nbt.getBoolean(TOY_ARROW_ON_FIRE_NBT_KEY);
        }

        if (hasAnyData |= nbt.contains(TOY_ARROW_OWNER_UUID_NBT_KEY)) {
            data.ownerUUID = nbt.getUuid(TOY_ARROW_OWNER_UUID_NBT_KEY);
        }

        // If all nbt keys are missing, then the arrow data wasn't saved
        return hasAnyData ? data : null;
    }

    public void saveDataTo(NbtCompound nbt) {
        nbt.putInt(TOY_ARROW_LEVEL_NBT_KEY, toyBowLevel);
        nbt.putDouble(TOY_ARROW_DAMAGE_NBT_KEY, damage);
        nbt.putInt(TOY_ARROW_PUNCH_NBT_KEY, punch);
        nbt.putBoolean(TOY_ARROW_ON_FIRE_NBT_KEY, onFire);

        if (ownerUUID != null) {
            nbt.putUuid(TOY_ARROW_OWNER_UUID_NBT_KEY, ownerUUID);
        }
    }
}
