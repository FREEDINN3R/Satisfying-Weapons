package net.freedinner.satisfying_weapons.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WishingStarItem extends UnfulfilledWishItem {
    public WishingStarItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setStackInHand(hand, new ItemStack(Items.NETHERITE_INGOT));

        return TypedActionResult.success(itemStack);
    }
}
