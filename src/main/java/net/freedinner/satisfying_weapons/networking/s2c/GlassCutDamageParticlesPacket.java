package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class GlassCutDamageParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d targetPos = new Vec3d(buf.readVector3f());
        float targetHeight = buf.readFloat();
        Vec3d particlePos = targetPos.add(0, targetHeight * MathUtils.randomNumber(0.3, 0.8), 0);
        boolean shouldSlash = buf.readBoolean();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Blood splatter

            for (int i = 0; i < 15; i++) {
                Vec3d dir = MathUtils.randomPointInSphere().multiply(1, 0.3, 1).normalize();
                Vec3d v = dir.multiply(MathUtils.randomNumber(0.1, 0.35));

                world.addParticle(ModParticles.BLOOD, particlePos.x, particlePos.y, particlePos.z, v.x, v.y, v.z);
            }

            // Slash particle

            if (shouldSlash) {
                DefaultParticleType slashParticle = MathUtils.getRandomElement(ModParticles.GLASS_CUT_SLASH);
                world.addParticle(slashParticle, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
            }
        });
    }
}