package net.freedinner.satisfying_weapons.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class PosUtils {
    public static BlockPos toBlockPos(Vec3d v) {
        return new BlockPos(
                (int) Math.round(v.x),
                (int) Math.round(v.y),
                (int) Math.round(v.z)
        );
    }

    public static BlockPos toBlockPos(Vector3f v) {
        return new BlockPos(
                Math.round(v.x),
                Math.round(v.y),
                Math.round(v.z)
        );
    }

    public static BlockPos toBlockPos(double x, double y, double z) {
        return new BlockPos(
                (int) Math.round(x),
                (int) Math.round(y),
                (int) Math.round(z)
        );
    }
}
