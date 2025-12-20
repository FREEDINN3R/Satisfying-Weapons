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
        Vec3d centerPos = new Vec3d(buf.readVector3f());
        Set<CrimsonKatanaItem.DoT> inflictedDots = buf.readEnumSet(CrimsonKatanaItem.DoT.class);

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Crimson sparks

            for (int i = 0; i < 10; i++) {
                Vec3d direction = MathUtils.randomPointInSphere().normalize();
                Vec3d finalPos = centerPos.add(direction.multiply(MathUtils.randomNumber(0.8, 1.2)));

                double stepSize = 0.05;
                int length = MathUtils.randomNumber(3, 5);
                Vec3d step = direction.multiply(stepSize);

                for (int j = 0; j < length; j++) {
                    Vec3d segmentPos = centerPos.add(step.multiply(j));
                    Vec3d v = finalPos.subtract(segmentPos).multiply(1.0 / CrimsonSparkParticle.MAX_AGE);

                    world.addParticle(ModParticles.CRIMSON_SPARK, segmentPos.x, segmentPos.y, segmentPos.z, v.x, v.y, v.z);
                }
            }
        });
    }
}
