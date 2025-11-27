package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class NaviaItem extends Item {
    public NaviaItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoundEvent sound = user.getName().getString().equals("FREEDINNER") ? ModSounds.NAVIA_2 : ModSounds.NAVIA_1;
            world.playSound(null, user.getBlockPos(), sound, SoundCategory.MASTER, 10f, 1f);

            user.getItemCooldownManager().set(this, 70);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
