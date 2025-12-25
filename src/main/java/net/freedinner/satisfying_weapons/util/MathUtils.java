package net.freedinner.satisfying_weapons.util;

import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class MathUtils {
    private static final Random random = new Random();

    public static double randomNumber(double bound) {
        return random.nextDouble(bound);
    }

    public static double randomNumber(double min, double max) {
        return min + random.nextDouble(max - min);
    }

    public static float randomNumber(float bound) {
        return random.nextFloat(bound);
    }

    public static float randomNumber(float min, float max) {
        return min + random.nextFloat(max - min);
    }

    public static int randomNumber(int bound) {
        return random.nextInt(bound);
    }

    public static int randomNumber(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public static boolean takeChance(double chance) {
        return random.nextDouble() < chance;
    }

    public static boolean takeChance(double chance, World world) {
        return world.getRandom().nextDouble() < chance;
    }

    public static <T> T randomElementFrom(List<T> list) {
        return randomElementFrom(list, false);
    }

    public static <T> T randomElementFrom(List<T> list, boolean removeElement) {
        int i = randomNumber(list.size());
        T element = list.get(i);

        if (removeElement) {
            list.remove(i);
        }

        return element;
    }
}

