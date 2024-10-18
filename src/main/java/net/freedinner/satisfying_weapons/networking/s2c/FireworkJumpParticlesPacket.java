package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class FireworkJumpParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vector3f pos = buf.readVector3f();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            int count = 80;
            for (int i = 0; i < count; i++) {
                double angle = Math.toRadians(360.0 / count * i);

                Vector3d v = new Vector3d(Math.cos(angle), 0, Math.sin(angle)).normalize().mul(0.4);
                world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, v.x, v.y, v.z);
            }
        });
    }
}
