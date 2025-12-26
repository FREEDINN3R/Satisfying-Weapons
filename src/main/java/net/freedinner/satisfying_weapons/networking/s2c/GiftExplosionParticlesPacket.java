package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.particle.ModParticles;
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

public class GiftExplosionParticlesPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
        Vec3d giftPos = new Vec3d(buf.readVector3f());

        ParticleEffect whiteWoolParticle = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Blocks.WHITE_WOOL));
        ParticleEffect redWoolParticle = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Blocks.RED_WOOL));

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Flash

            world.addParticle(ParticleTypes.FLASH, giftPos.x, giftPos.y, giftPos.z, 0, 0, 0);

            // Confetti

            for (int i = 0; i < 250; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.4, 1.2);
                world.addParticle(ModParticles.CONFETTI, giftPos.x, giftPos.y, giftPos.z, v.x, v.y, v.z);
            }

            // Wool particles

            for (int i = 0; i < 120; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.3, 0.5);
                world.addParticle(i % 5 == 0 ? whiteWoolParticle : redWoolParticle, giftPos.x, giftPos.y, giftPos.z, v.x, v.y, v.z);
            }
        });
    }
}
