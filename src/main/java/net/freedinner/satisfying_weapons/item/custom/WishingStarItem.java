package net.freedinner.satisfying_weapons.item.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

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
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 50;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            return TypedActionResult.pass(itemStack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        switch (remainingUseTicks) {
            case 1:
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1f, 1f);
            case 10:
            case 20:
            case 30:
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 0.6f, PitchUtils.get(0.05f));
        }
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
        ItemStack randomStack = items.get(world.getRandom().nextInt(items.size()));

        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(randomStack.getItem(), 10);
        }

        this.sendParticlesPacket(world, user.getEyePos().toVector3f());

        return randomStack;
    }

    private void sendParticlesPacket(World world, Vector3f pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(pos);

        BlockPos blockPos = new BlockPos(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, blockPos)) {
            ServerPlayNetworking.send(player, ModNetworking.WISHING_STAR_PARTICLES_ID, buf);
        }
    }
}
