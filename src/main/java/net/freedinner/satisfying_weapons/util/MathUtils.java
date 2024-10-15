package net.freedinner.satisfying_weapons.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class MathUtils {
    private static final Random random = new Random();

    public static Vec3d randomPointInSphere() {
        return randomPointInSphere(1);
    }

    public static Vec3d randomPointInSphere(double radius) {
        double r = Math.cbrt(random.nextDouble()) * radius;
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(2 * random.nextDouble() - 1);

        float x = (float) (r * Math.sin(phi) * Math.cos(theta));
        float y = (float) (r * Math.sin(phi) * Math.sin(theta));
        float z = (float) (r * Math.cos(phi));

        return new Vec3d(x, y, z);
    }

    public static double randomDouble(double bound) {
        return randomDouble(0, bound);
    }

    public static double randomDouble(double base, double flex) {
        return base + random.nextDouble(flex);
    }

    public static int randomInt(int bound) {
        return randomInt(0, bound);
    }

    public static int randomInt(int base, int flex) {
        return base + random.nextInt(flex);
    }

    public static boolean takeChance(double chance) {
        return random.nextDouble() < chance;
    }

    public static boolean takeChance(double chance, World world) {
        return world.getRandom().nextDouble() < chance;
    }
}

