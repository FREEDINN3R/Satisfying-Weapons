package net.freedinner.satisfying_weapons.networking.s2c;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
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
        Vec3d centerPos = new Vec3d(buf.readVector3f());
        boolean hasRolledWeapon = buf.readBoolean();

        client.execute(() -> {
            World world = client.world;

            if (world == null) {
                return;
            }

            // Broken star shards

            ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(ModItems.WISHING_STAR));

            for (int i = 0; i < 30; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.5, 0.7);
                world.addParticle(particle, centerPos.x, centerPos.y, centerPos.z, v.x, v.y * 0.3, v.z);
            }

            // Weapon drop particles

            if (!hasRolledWeapon) {
                return;
            }

            for (int i = 0; i < 15; i++) {
                Vec3d dir = ParticleUtils.randomDirection();
                Vec3d offset = dir.multiply(Math.pow(MathUtils.randomNumber(0.8, 1.0), 2)).multiply(1, 0.75, 1);
                Vec3d particlePos = centerPos.add(offset);

                world.addParticle(ParticleTypes.HAPPY_VILLAGER, particlePos.x, particlePos.y, particlePos.z, 0, 0, 0);
            }
        });
    }
}
