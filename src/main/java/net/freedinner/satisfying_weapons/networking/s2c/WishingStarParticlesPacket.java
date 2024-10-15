package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WishingStarParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d eyePos = new Vec3d(buf.readVector3f());

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(ModItems.WISHING_STAR));

            for (int i = 0; i < 30; i++) {
                Vec3d v = MathUtils.randomPointInSphere();
                v = v.normalize().multiply(MathUtils.randomDouble(0.5, 0.2));

                world.addParticle(particle, eyePos.x, eyePos.y - 0.3, eyePos.z, v.x, v.y * 0.3, v.z);
            }
        });
    }
}
