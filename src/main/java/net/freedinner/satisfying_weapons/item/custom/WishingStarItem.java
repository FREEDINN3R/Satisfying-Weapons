package net.freedinner.satisfying_weapons.item.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WishingStarItem extends Item {
    public static final double WEAPON_DROP_CHANCE = 0.25;

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
        if (world.isClient) {
            return stack;
        }

        // Roll either a weapon or chest loot
        boolean hasRolledWeapon = MathUtils.takeChance(WEAPON_DROP_CHANCE, world);
        ItemStack rolledStack = (hasRolledWeapon) ? rollRandomWeapon(world) : rollRandomChestLoot(world);

        // Prevent accidentally using the new item
        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(rolledStack.getItem(), 10);
        }

        // Visuals & SFX
        this.sendParticlesPacket(world, user.getEyePos().toVector3f(), hasRolledWeapon);
        if (hasRolledWeapon){
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 1f);
        }
        else {
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1f, 1f);
        }

        return rolledStack;
    }

    private ItemStack rollRandomChestLoot(World world) {
        // Get all existing chest loot tables
        List<Identifier> allLootTables = LootTables.getAll()
                .stream()
                .filter(id -> id.getPath().contains("chests/"))
                .toList();

        // Pick random chest loot table
        Identifier randomId = allLootTables.get(world.getRandom().nextInt(allLootTables.size()));
        LootTable lootTable = world.getServer().getLootManager().getLootTable(randomId);

        // Generate a random item stack from that chest
        ObjectArrayList<ItemStack> items = lootTable.generateLoot(new LootContextParameterSet.Builder((ServerWorld) world).add(LootContextParameters.ORIGIN, Vec3d.ZERO).build(LootContextTypes.CHEST));
        return items.get(world.getRandom().nextInt(items.size()));
    }

    private ItemStack rollRandomWeapon(World world) {
        // TODO: properly implement chances based on weapon rarity

        List<Item> allWeapons = Arrays.asList(
                ModItems.FIREWORK_SWORD
        );

        Item randomWeapon = allWeapons.get(world.getRandom().nextInt(allWeapons.size()));
        return new ItemStack(randomWeapon);
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
