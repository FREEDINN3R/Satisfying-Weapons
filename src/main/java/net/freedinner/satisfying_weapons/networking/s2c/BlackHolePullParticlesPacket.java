package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlackHolePullParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d center = new Vec3d(buf.readVector3f());
        double radius = buf.readDouble();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // White lines

            for (int i = 0; i < 5; i++) {
                Vec3d direction;

                do {
                    direction = MathUtils.randomPointInSphere(1.5 * radius);
                }
                while (direction.length() < 1);

                Vec3d step = direction.normalize().multiply(0.08);
                Vec3d v = direction.multiply(-0.03);
                int length = MathUtils.randomNumber(10, 20);

                for (int j = 0; j < length; j++) {
                    Vec3d pos = center.add(direction).add(step.multiply(j));
                    world.addParticle(ModParticles.WHITE_LINE, pos.x, pos.y, pos.z, v.x, v.y, v.z);
                }
            }

            // Dark lines

            for (int i = 0; i < 5; i++) {
                Vec3d direction;

                do {
                    direction = MathUtils.randomPointInSphere(1.5 * radius);
                }
                while (direction.length() < 1);

                Vec3d step = direction.normalize().multiply(0.08);
                Vec3d v = direction.multiply(-0.03);
                int length = MathUtils.randomNumber(10, 20);

                for (int j = 0; j < length; j++) {
                    Vec3d pos = center.add(direction).add(step.multiply(j));
                    world.addParticle(ModParticles.DARK_LINE, pos.x, pos.y, pos.z, v.x, v.y, v.z);
                }
            }

            // Block debris

            for (int i = 0; i < 200; i++) {
                Vec3d direction = MathUtils.randomPointInSphere(radius);
                Vec3d pos = center.add(direction);
                pos = pos.add(0, (pos.y < 0) ? 1 : -1, 0);
                Vec3d v = direction.multiply(-0.1).multiply(MathUtils.randomNumber(0.2, 1.0));

                BlockState block = world.getBlockState(PosUtils.toBlockPos(pos));
                pos = pos.add(0, (v.y > 0) ? 1 : -1, 0);
                BlockState blockAbove = world.getBlockState(PosUtils.toBlockPos(pos));

                if (block.isAir() || !blockAbove.isAir()) {
                    continue;
                }

                ParticleEffect particle = (block.isLiquid()) ? new BlockStateParticleEffect(ParticleTypes.BLOCK, block)
                        : new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(block.getBlock()));

                world.addParticle(particle, pos.x, pos.y, pos.z, v.x, v.y, v.z);
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
