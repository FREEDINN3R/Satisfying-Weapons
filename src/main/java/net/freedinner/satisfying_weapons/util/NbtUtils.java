package net.freedinner.satisfying_weapons.util;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class NbtUtils {
    public static int getOrCreate(NbtCompound nbt, String nbtKey, int defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putInt(nbtKey, defaultValue);
        }

        return nbt.getInt(nbtKey);
    }

    public static long getOrCreate(NbtCompound nbt, String nbtKey, long defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putLong(nbtKey, defaultValue);
        }

        return nbt.getLong(nbtKey);
    }

    public static double getOrCreate(NbtCompound nbt, String nbtKey, double defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putDouble(nbtKey, defaultValue);
        }

        return nbt.getDouble(nbtKey);
    }

    public static boolean getOrCreate(NbtCompound nbt, String nbtKey, boolean defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putBoolean(nbtKey, defaultValue);
        }

        return nbt.getBoolean(nbtKey);
    }

    public static UUID getOrCreate(NbtCompound nbt, String nbtKey, UUID defaultValue) {
        if (!nbt.contains(nbtKey)) {
            nbt.putUuid(nbtKey, defaultValue);
        }

        return nbt.getUuid(nbtKey);
    }

    public static void putIfExists(NbtCompound nbt, String nbtKey, UUID value) {
        if (value != null) {
            nbt.putUuid(nbtKey, value); // Null UUID would crash the game
        }
    }
}
