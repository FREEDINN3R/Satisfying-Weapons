package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.util.MathUtils;
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

public class GlassCutDamageParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d targetPos = new Vec3d(buf.readVector3f());
        Vec3d slashPos = new Vec3d(buf.readVector3f());

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Blood splatter

            for (int i = 0; i < 10; i++) {
                Vec3d v = MathUtils.randomPointInSphere().multiply(1, 0.3, 1);
                v = v.normalize().multiply(MathUtils.randomNumber(0.1, 0.25));

                world.addParticle(ModParticles.BLOOD, targetPos.x, targetPos.y, targetPos.z, v.x, v.y, v.z);
            }

            // Slash particle

            world.addParticle(ModParticles.GLASS_CUT_SLASH, slashPos.x, slashPos.y, slashPos.z, 0, 0, 0);
        });
    }
}