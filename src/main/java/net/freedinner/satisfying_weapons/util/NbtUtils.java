package net.freedinner.satisfying_weapons.util;

import net.minecraft.nbt.NbtCompound;

public class NbtUtils {
    public static int getOrCreate(NbtCompound nbt, String nbtKey, int defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putInt(nbtKey, defaultValue);
        }

        return nbt.getInt(nbtKey);
    }

    public static boolean getOrCreate(NbtCompound nbt, String nbtKey, boolean defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putBoolean(nbtKey, defaultValue);
        }

        return nbt.getBoolean(nbtKey);
    }
}
