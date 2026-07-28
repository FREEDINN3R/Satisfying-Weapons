package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkTrailParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d entityCenter = new Vec3d(buf.readVector3f());
        double entityVelY = buf.readDouble();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Firework trail

            int particleCount = (entityVelY > 0) ? 1 : 3;
            double trailVelY = -0.5 * Math.signum(entityVelY);

            for (int i = 0; i < particleCount; i++) {
                Vec3d pos = entityCenter.add(ParticleUtils.randomPointInSphere(0.4));
                world.addParticle(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 0, trailVelY, 0);
            }


            // Smoke

            for (int i = 0; i < 3 * particleCount; i++) {
                Vec3d smokeVel = ParticleUtils.randomDirection();
                smokeVel = smokeVel.add(0, 2 * Math.signum(smokeVel.y), 0).normalize();

                if (smokeVel.y * entityVelY > 0) {
                    smokeVel = smokeVel.multiply(-1);
                }

                world.addParticle(ParticleTypes.SMOKE, entityCenter.x, entityCenter.y, entityCenter.z, smokeVel.x, smokeVel.y, smokeVel.z);
            }


        });
    }
}