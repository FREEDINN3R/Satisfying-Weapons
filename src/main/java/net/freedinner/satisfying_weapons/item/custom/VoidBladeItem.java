package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.freedinner.satisfying_weapons.util.PosUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class VoidBladeItem extends UpgradeableSwordItem {
    public VoidBladeItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable VoidBladeItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }
}
