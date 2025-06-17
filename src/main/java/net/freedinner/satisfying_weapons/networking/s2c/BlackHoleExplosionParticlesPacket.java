package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlackHoleExplosionParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d pos = new Vec3d(buf.readVector3f());

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Flash
            for (int i = 0; i < 6; i++) {
                Vec3d d = MathUtils.randomPointInSphere().normalize().multiply(2);

                world.addParticle(ParticleTypes.FLASH, pos.x + d.x, pos.y + d.y, pos.z + d.z, 0, 0, 0);
            }

            // Shockwave
            for (int i = 0; i < 120; i++) {
                double angle = Math.toRadians(3 * i);
                Vec3d v = new Vec3d(Math.sin(angle), 0, Math.cos(angle));
                v = v.normalize().multiply(4);

                world.addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, v.x, v.y, v.z);
            }

            // Smoke
            for (int i = 0; i < 120; i++) {
                Vec3d v = MathUtils.randomPointInSphere();
                v = v.normalize().multiply(MathUtils.randomNumber(0.2, 0.5));

                world.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, v.x, v.y * 0.5, v.z);
            }

            // Entropy particles
            for (int i = 0; i < 80; i++) {
                Vec3d v = MathUtils.randomPointInSphere();
                v = v.normalize().multiply(MathUtils.randomNumber(0.6, 1.2));

                world.addParticle(ModParticles.ENTROPY, pos.x, pos.y, pos.z, v.x, v.y, v.z);
            }
        });
    }
}
