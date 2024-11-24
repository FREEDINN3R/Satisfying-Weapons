package net.freedinner.satisfying_weapons.entity.custom;

import net.minecraft.nbt.NbtCompound;

public class ToyArrowEntityData {
    public int toyBowLevel = 5;
    public double damage = 5;
    public int punch = 0;
    public boolean onFire = false;

    private static final String TOY_ARROW_DATA_LEVEL_NBT_KEY = "toy_arrow_data_level";
    private static final String TOY_ARROW_DATA_DAMAGE_NBT_KEY = "toy_arrow_damage_level";
    private static final String TOY_ARROW_DATA_PUNCH_NBT_KEY = "toy_arrow_punch_level";
    private static final String TOY_ARROW_DATA_ON_FIRE_NBT_KEY = "toy_arrow_on_fire_level";

    public ToyArrowEntityData() {
    }

    private ToyArrowEntityData(ToyArrowEntity toyArrow) {
        this.toyBowLevel = toyArrow.getToyBowLevel();
        this.damage = toyArrow.getDamage();
        this.punch = toyArrow.getPunch();
        this.onFire = toyArrow.isOnFire();
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
    }

    public static ToyArrowEntityData loadDataFrom(NbtCompound nbt) {
        ToyArrowEntityData data = new ToyArrowEntityData();

        if (nbt.contains(TOY_ARROW_DATA_LEVEL_NBT_KEY)) {
            data.toyBowLevel = nbt.getInt(TOY_ARROW_DATA_LEVEL_NBT_KEY);
        }

        if (nbt.contains(TOY_ARROW_DATA_DAMAGE_NBT_KEY)) {
            data.damage = nbt.getDouble(TOY_ARROW_DATA_DAMAGE_NBT_KEY);
        }

        if (nbt.contains(TOY_ARROW_DATA_PUNCH_NBT_KEY)) {
            data.punch = nbt.getInt(TOY_ARROW_DATA_PUNCH_NBT_KEY);
        }

        if (nbt.contains(TOY_ARROW_DATA_ON_FIRE_NBT_KEY)) {
            data.onFire = nbt.getBoolean(TOY_ARROW_DATA_ON_FIRE_NBT_KEY);
        }

        return data;
    }

    public void saveDataTo(NbtCompound nbt) {
        nbt.putInt(TOY_ARROW_DATA_LEVEL_NBT_KEY, toyBowLevel);
        nbt.putDouble(TOY_ARROW_DATA_DAMAGE_NBT_KEY, damage);
        nbt.putInt(TOY_ARROW_DATA_PUNCH_NBT_KEY, punch);
        nbt.putBoolean(TOY_ARROW_DATA_ON_FIRE_NBT_KEY, onFire);
    }
}
