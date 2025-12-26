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
        Vec3d entityPos = new Vec3d(buf.readVector3f());
        double yVelocity = buf.readDouble();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Firework trail

            int count = (yVelocity > 0) ? 1 : 3;
            double particleVelocity = -0.5 * Math.signum(yVelocity);

            for (int i = 0; i < count; i++) {
                Vec3d pos = entityPos.add(ParticleUtils.randomPointInSphere(0.4)).add(0, 0.5, 0);
                world.addParticle(ParticleTypes.FIREWORK, pos.x, pos.y, pos.z, 0, particleVelocity, 0);
            }

            // Smoke

            Vec3d pos = entityPos.add(ParticleUtils.randomPointInSphere(0.4)).add(0, 0.5, 0);
            world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 0, particleVelocity, 0);
        });
    }
}