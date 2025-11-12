package net.freedinner.satisfying_weapons.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
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
        if (chance <= 0) {
            return false;
        }

        return random.nextDouble() < chance;
    }

    public static boolean takeChance(double chance, World world) {
        if (chance <= 0) {
            return false;
        }

        return world.getRandom().nextDouble() < chance;
    }

    public static Vec3d getViewHitboxIntersection(Entity target, Entity viewer) {
        // This method gives the point where the "view" vector from viewer to target intersects the hitbox
        // Honestly idk what exactly is happening here, I use it for particles

        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
        Vec3d viewerPos = viewer.getEyePos();

        Vec3d dist = viewerPos.subtract(targetPos);
        Vec3d dir = dist.normalize();

        if (dist.lengthSquared() == 0) {
            return targetPos;
        }

        Box hitbox = target.getBoundingBox();
        double hx = (hitbox.maxX - hitbox.minX) * 0.5;
        double hy = (hitbox.maxY - hitbox.minY) * 0.5;
        double hz = (hitbox.maxZ - hitbox.minZ) * 0.5;

        double tx = dir.x != 0 ? hx / Math.abs(dir.x) : Double.POSITIVE_INFINITY;
        double ty = dir.y != 0 ? hy / Math.abs(dir.y) : Double.POSITIVE_INFINITY;
        double tz = dir.z != 0 ? hz / Math.abs(dir.z) : Double.POSITIVE_INFINITY;

        // Smallest t gives the first interior face hit
        double t = Math.min(tx, Math.min(ty, tz));

        return targetPos.add(dir.multiply(t));
    }
}

