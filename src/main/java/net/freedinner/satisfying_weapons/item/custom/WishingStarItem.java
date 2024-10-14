package net.freedinner.satisfying_weapons.item.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class WishingStarItem extends Item {
    public WishingStarItem(Settings settings) {
        super(settings);
    }

    public static float getWishProgress(int useTicks) {
        return useTicks / 50f;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 50;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (world.isClient) {
            return stack;
        }

        List<Identifier> allLootTables = LootTables.getAll()
                .stream()
                .filter(id -> id.getPath().contains("chests/"))
                .toList();
        Identifier randomId = allLootTables.get(world.getRandom().nextInt(allLootTables.size()));
        LootTable lootTable = world.getServer().getLootManager().getLootTable(randomId);

        ObjectArrayList<ItemStack> items = lootTable.generateLoot(new LootContextParameterSet.Builder((ServerWorld) world).add(LootContextParameters.ORIGIN, Vec3d.ZERO).build(LootContextTypes.CHEST));
        return items.get(world.getRandom().nextInt(items.size()));
    }
}
