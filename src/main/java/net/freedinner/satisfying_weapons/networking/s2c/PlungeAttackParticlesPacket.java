package net.freedinner.satisfying_weapons.networking.s2c;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.ParticleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

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

            // Plunge ring

            int count = 90;
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

            ParticleEffect particle = (landingBlock.isLiquid()) ? new BlockStateParticleEffect(ParticleTypes.BLOCK, landingBlock)
                    : new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(landingBlock.getBlock()));

            for (int i = 0; i < 120; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.4, 0.8);
                v = v.multiply((v.y < 0) ? -1 : 1);
                world.addParticle(particle, playerPos.x, playerPos.y + 0.5, playerPos.z, v.x, Math.abs(v.y) * 0.3, v.z);
            }

            // Firework explosion

            List<String> fireworkList = Arrays.asList(
                    "{Explosions:[{Colors:[I;15790320,14602026,14602026,8073150,8073150],Flicker:1b,Trail:1b,Type:1b}],Flight:2b}",
                    "{Explosions:[{Colors:[I;15790320,11743532,4312372,11743532,4312372],Flicker:1b,Trail:1b,Type:1b}],Flight:2b}",
                    "{Explosions:[{Colors:[I;2437522,14602026,2437522,15435844],Flicker:1b,Trail:1b,Type:1b}],Flight:1b}",
                    "{Explosions:[{Colors:[I;8073150,2437522],FadeColors:[I;15790320],Flicker:1b,Type:1b},{Colors:[I;8073150,2437522],FadeColors:[I;15790320],Flicker:1b,Type:1b}],Flight:2b}",
                    "{Explosions:[{Colors:[I;4312372,14602026],FadeColors:[I;15790320],Flicker:1b,Type:1b},{Colors:[I;4312372,14602026],FadeColors:[I;15790320],Flicker:1b,Type:1b}],Flight:2b}"
            );

            String fireworkNbtString = MathUtils.randomElementFrom(fireworkList);
            NbtCompound fireworkNbt = null;

            try {
                fireworkNbt = StringNbtReader.parse(fireworkNbtString);
            } catch (CommandSyntaxException e) {
                SatisfyingWeapons.LOGGER.error("Firework NBT parsing failed: " + fireworkNbtString);
            }

            world.addFireworkParticle(playerPos.x, playerPos.y, playerPos.z, 0, 0, 0, fireworkNbt);

            // Sparkles

            for (int i = 0; i < 20; i++) {
                Vec3d v = ParticleUtils.randomDirVelocity(0.4, 1.0);
                world.addParticle(ParticleTypes.END_ROD, playerPos.x, playerPos.y, playerPos.z, v.x, v.y * 0.8, v.z);
            }
        });
    }
}
