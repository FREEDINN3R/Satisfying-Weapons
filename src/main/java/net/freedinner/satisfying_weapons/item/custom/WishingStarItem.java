package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.PlayerWishDataManager;
import net.freedinner.satisfying_weapons.datagen.ModTags;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

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
        // Play glass break sound every 0.5 seconds
        switch (remainingUseTicks) {
            case 1:
            case 10:
            case 20:
            case 30:
                world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.MASTER, 0.6f, PitchUtils.get(0.05f));
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!(user instanceof ServerPlayerEntity serverPlayer)) {
            return stack;
        }

        // Roll an item / weapon
        ItemStack rolledStack = PlayerWishDataManager.rollForPlayer(serverPlayer);

        // Prevents accidentally using the new item
        serverPlayer.getItemCooldownManager().set(rolledStack.getItem(), 10);

        // Visuals & SFX
        this.sendParticlesPacket(world, user.getEyePos().toVector3f(), rolledStack.isIn(ModTags.ALL_MOD_WEAPONS));
        if (rolledStack.isIn(ModTags.ALL_MOD_WEAPONS) || rolledStack.isOf(ModItems.NAVIA)){
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 1f);
        }
        else {
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1f, 1f);
        }

        return rolledStack;
    }

    private void sendParticlesPacket(World world, Vector3f pos, boolean hasRolledWeapon) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVector3f(pos);
        buf.writeBoolean(hasRolledWeapon);

        BlockPos blockPos = new BlockPos(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, blockPos)) {
            ServerPlayNetworking.send(player, ModNetworking.WISHING_STAR_PARTICLES_ID, buf);
        }
    }
}
