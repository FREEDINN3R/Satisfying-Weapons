package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class GlassShatterParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vector3f particlePos = buf.readVector3f();
        ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Blocks.GLASS));

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            for (int i = 0; i < 60; i++) {
                double dx = MathUtils.randomNumber(-0.25, 0.25);
                double dz = MathUtils.randomNumber(-0.25, 0.25);

                Vec3d v = ParticleUtils.randomDirVelocity(0.3, 0.5);

                world.addParticle(particle, particlePos.x + dx, particlePos.y, particlePos.z + dz, v.x, v.y * 0.5, v.z);
            }
        });
    }
}