package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntropyParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d particlePos = new Vec3d(buf.readVector3f());
        Vec3d entityPos = new Vec3d(buf.readVector3f());

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            Vec3d v = particlePos.subtract(entityPos)
                    .multiply(1, 0, 1)
                    .normalize()
                    .multiply(MathUtils.randomNumber(0.05, 0.12));

            world.addParticle(ModParticles.ENTROPY, particlePos.x, particlePos.y, particlePos.z, v.x, v.y, v.z);
        });
    }
}
