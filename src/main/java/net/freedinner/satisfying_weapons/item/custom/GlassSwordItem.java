package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GlassSwordItem extends UpgradeableSwordItem {
    public GlassSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable GlassSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean broken = MathUtils.takeChance(this.getLevel() > 1 ? 0.5 : 1.0);

        if (broken) {
            sendParticlesPacket(attacker);
            attacker.getWorld().playSound(null, attacker.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 1.0f, PitchUtils.get());

            attacker.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.BROKEN_GLASS_SWORD));
        }

        return true;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    private void sendParticlesPacket(LivingEntity attacker) {
        Vector3f particlePos = attacker.getPos().toVector3f();
        particlePos.add(0, attacker.getHeight() * 0.3f, 0);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(particlePos);

        BlockPos blockPos = PosUtils.toBlockPos(attacker.getPos());

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld)attacker.getWorld(), blockPos)) {
            ServerPlayNetworking.send(player, ModNetworking.GLASS_SWORD_PARTICLES_ID, buf);
        }
    }
}
