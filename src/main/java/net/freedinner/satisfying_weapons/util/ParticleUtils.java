package net.freedinner.satisfying_weapons.util;

import net.minecraft.util.math.Vec3d;

public class ParticleUtils {
    public static Vec3d randomPointInSphere() {
        return randomPointInSphere(1);
    }

    public static Vec3d randomPointInSphere(double radius) {
        double r = Math.cbrt(MathUtils.randomNumber(1.0)) * radius;
        double theta = MathUtils.randomNumber(1.0) * 2 * Math.PI;
        double phi = Math.acos(2 * MathUtils.randomNumber(1.0) - 1);

        float x = (float) (r * Math.sin(phi) * Math.cos(theta));
        float y = (float) (r * Math.sin(phi) * Math.sin(theta));
        float z = (float) (r * Math.cos(phi));

        return new Vec3d(x, y, z);
    }

    public static Vec3d randomDirection() {
        return randomPointInSphere().normalize();
    }

    public static Vec3d randomFlatDirection() {
        return randomPointInSphere().multiply(1, 0, 1).normalize();
    }

    public static Vec3d randomDirVelocity(double min, double max) {
        return randomDirection().multiply(MathUtils.randomNumber(min, max));
    }
}
