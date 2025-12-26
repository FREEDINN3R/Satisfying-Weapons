package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FestivityGainedParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d entityPos = new Vec3d(buf.readVector3f());
        Vec3d viewerDirection = new Vec3d(buf.readVector3f());
        double width = buf.readDouble();
        double height = buf.readDouble();
        int festivityStacks = buf.readInt();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Smoke and flames

            int totalCount = 10 + (int) (height * width / 0.12);
            int flameCount = totalCount / 4;

            for (int i = 0; i < totalCount; i++) {
                double dx = 0.5 * (width / 2 - MathUtils.randomNumber(width));
                double dy = 0.3 + 0.6 * MathUtils.randomNumber(height);
                double dz = 0.5 * (width / 2 - MathUtils.randomNumber(width));

                Vec3d v = ParticleUtils.randomFlatDirection().multiply(MathUtils.randomNumber(0.06, 0.1));

                // If 3/6/9 Festivity stacks, add flame particles
                ParticleEffect particle = (festivityStacks % 3 == 0 && i < flameCount) ? ParticleTypes.FLAME : ParticleTypes.SMOKE;
                world.addParticle(particle, entityPos.x + dx, entityPos.y + dy, entityPos.z + dz, v.x, v.y, v.z);
            }

            // Festivity stacks count

            ParticleEffect particle = ModParticles.FESTIVITY_COUNT.get(festivityStacks - 1);

            Vec3d countPos = entityPos.add(0, height, 0);
            Vec3d modifiedDir = viewerDirection.multiply(1, 0,1).normalize();
            modifiedDir = new Vec3d(-modifiedDir.z, 0, modifiedDir.x).multiply(width / 2 + 0.1);
            countPos = countPos.add(modifiedDir);

            world.addParticle(particle, countPos.x, countPos.y, countPos.z, 0, 0.02, 0);
        });
    }
}
