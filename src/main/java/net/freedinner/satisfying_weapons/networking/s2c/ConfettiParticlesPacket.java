package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConfettiParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d entityCenter = new Vec3d(buf.readVector3f());
        int particleCount = buf.readInt();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Colorful confetti

            for (int i = 0; i < particleCount; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.2, 0.5);
                Vec3d particlePos = entityCenter.add(ParticleUtils.randomPointInSphere(0.25));
                world.addParticle(ModParticles.CONFETTI, particlePos.x, particlePos.y, particlePos.z, v.x, v.y, v.z);
            }
        });
    }
}