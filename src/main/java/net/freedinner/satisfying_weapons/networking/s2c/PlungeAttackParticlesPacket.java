package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

@SuppressWarnings("deprecation")
public class PlungeAttackParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vector3f playerPos = buf.readVector3f();
        BlockPos landingBlockPos = buf.readBlockPos();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Flash

            world.addParticle(ParticleTypes.FLASH, playerPos.x, playerPos.y, playerPos.z, 0, 0, 0);

            // Sparkles

            for (int i = 0; i < 100; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.4, 1.0);
                world.addParticle(ParticleTypes.END_ROD, playerPos.x, playerPos.y, playerPos.z, v.x, v.y * 0.8, v.z);
            }

            // Plunge ring

            int count = 100;
            for (int i = 0; i < count; i++) {
                double angle = Math.toRadians(360.0 / count * i);

                Vector3d v = new Vector3d(Math.cos(angle), 0.2, Math.sin(angle)).normalize().mul(3.0);
                world.addParticle(ParticleTypes.CRIT, playerPos.x, playerPos.y + 0.1, playerPos.z, v.x, v.y, v.z);
            }

            // Block debris

            BlockState landingBlock = world.getBlockState(landingBlockPos);
            if (landingBlock.isAir() || landingBlock.isLiquid()) {
                return;
            }

            ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(landingBlock.getBlock()));

            for (int i = 0; i < 200; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.3, 0.8);
                world.addParticle(particle, playerPos.x, playerPos.y + 0.5, playerPos.z, v.x, Math.abs(v.y) * 0.3, v.z);
            }
        });
    }
}
