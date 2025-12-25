package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlackHolePullParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d centerPos = new Vec3d(buf.readVector3f());
        double radius = buf.readDouble();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // White and dark lines

            for (int i = 0; i < 10; i++) {
                Vec3d dir = ParticleUtils.randomDirection();
                Vec3d offset = dir.multiply(MathUtils.randomNumber(1.0, 1.5 * radius));
                Vec3d v = offset.multiply(-0.03);
                Vec3d step = dir.multiply(0.08);

                DefaultParticleType lineParticle = (i % 2 == 0) ? ModParticles.DARK_LINE : ModParticles.WHITE_LINE;
                int length = MathUtils.randomNumber(10, 20);

                for (int j = 0; j < length; j++) {
                    Vec3d pos = centerPos.add(offset).add(step.multiply(j));
                    world.addParticle(lineParticle, pos.x, pos.y, pos.z, v.x, v.y, v.z);
                }
            }

            // Block debris

            for (int i = 0; i < 200; i++) {
                Vec3d offset = ParticleUtils.randomPointInSphere(radius);
                Vec3d v = offset.multiply(-1).multiply(MathUtils.randomNumber(0.02, 0.1));
                Vec3d particlePos = centerPos.add(offset);

                BlockState sourceBlock = world.getBlockState(PosUtils.toBlockPos(particlePos));
                particlePos = particlePos.add(0, (v.y > 0) ? 1 : -1, 0);
                BlockState blockAbove = world.getBlockState(PosUtils.toBlockPos(particlePos));

                if (sourceBlock.isAir() || !blockAbove.isAir()) {
                    continue;
                }

                ParticleEffect particle = (sourceBlock.isLiquid()) ? new BlockStateParticleEffect(ParticleTypes.BLOCK, sourceBlock)
                        : new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(sourceBlock.getBlock()));

                world.addParticle(particle, particlePos.x, particlePos.y, particlePos.z, v.x, v.y, v.z);
            }

            // Wind

            /*for (int i = 0; i < 120; i++) {
                double angle = Math.toRadians(3 * i);

                double x = radius * Math.sin(angle);
                double z = radius * Math.cos(angle);

                Vec3d pos = center.add(x, 0, z);
                world.addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, 0, 0);
            }*/
        });
    }
}
