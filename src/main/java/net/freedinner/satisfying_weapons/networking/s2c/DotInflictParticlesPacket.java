package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.item.custom.CrimsonKatanaItem;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.particle.custom.CrimsonSparkParticle;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public class DotInflictParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Set<CrimsonKatanaItem.DoT> inflictedDots = buf.readEnumSet(CrimsonKatanaItem.DoT.class);
        Vec3d centerPos = new Vec3d(buf.readVector3f());
        Vec3d viewerEyePos = new Vec3d(buf.readVector3f());

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Crimson sparks

//            for (int i = 0; i < 6; i++) {
//                Vec3d direction = getRandomPerpendicularDirection(centerPos, viewerEyePos);
//                Vec3d finalPos = centerPos.add(direction.multiply(MathUtils.randomNumber(0.8, 1.5)));
//
//                double stepSize = 0.025;
//                int length = MathUtils.randomNumber(10, 16);
//                Vec3d step = direction.multiply(stepSize);
//                Vec3d glowOffset = centerPos.subtract(viewerEyePos).normalize().multiply(0.01);
//
//                for (int j = 0; j < length; j++) {
//                    Vec3d segmentPos = centerPos.add(step.multiply(j));
//                    Vec3d segmentFinalPos = finalPos.subtract(step.multiply(length / 2.0));
//                    Vec3d v = segmentFinalPos.subtract(segmentPos).multiply(1.0 / CrimsonSparkParticle.MAX_AGE);
//
//                    world.addParticle(ModParticles.CRIMSON_SPARK, segmentPos.x, segmentPos.y, segmentPos.z, v.x, v.y, v.z);
//                    if (j % 2 == 0) {
//                        Vec3d glowPos = segmentPos.add(glowOffset);
//                        world.addParticle(ModParticles.CRIMSON_SPARK_GLOW, glowPos.x, glowPos.y, glowPos.z, v.x, v.y, v.z);
//                    }
//                }
//            }
        });
    }

    public static Vec3d getRandomPerpendicularDirection(Vec3d centerPos, Vec3d viewerPos) {
        // Plane normal: direction from viewer to center
        Vec3d normal = centerPos.subtract(viewerPos).normalize();


        // Choose an arbitrary vector not parallel to the normal
        Vec3d arbitrary = Math.abs(normal.y) < 0.99
                ? new Vec3d(0, 1, 0)
                : new Vec3d(1, 0, 0);

        // First basis vector in the plane
        Vec3d u = normal.crossProduct(arbitrary).normalize();

        // Second basis vector in the plane
        Vec3d v = normal.crossProduct(u).normalize();

        // Random angle in [0, 2π)
        double angle = MathUtils.randomNumber(1.0) * Math.PI * 2.0;

        // Random direction in the plane starting from centerPos
        return u.multiply(Math.cos(angle)).add(v.multiply(Math.sin(angle)));
    }
}
