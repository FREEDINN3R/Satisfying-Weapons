package net.freedinner.satisfying_weapons.util;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

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

    public static Vec3d getEntityCenter(Entity entity) {
        return entity.getPos().add(0, entity.getHeight() * 0.5, 0);
    }

    public static Collection<ServerPlayerEntity> getPlayersTracking(Entity entity) {
        return getPlayersTracking(getEntityCenter(entity), entity.getWorld());
    }

    public static Collection<ServerPlayerEntity> getPlayersTracking(Vec3d pos, World world) {
        if (!(world instanceof ServerWorld serverWorld)) {
            return List.of();
        }

        return PlayerLookup.tracking(serverWorld, PosUtils.toBlockPos(pos));
    }
}