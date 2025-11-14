package net.freedinner.satisfying_weapons;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PlayerWishData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerWishDataManager extends PersistentState {
    private final HashMap<UUID, PlayerWishData> playersWishData;

    private static final String PLAYERS_WISH_DATA_NBT_KEY = "players_wish_data";

    public PlayerWishDataManager() {
        playersWishData = new HashMap<>();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersWishDataNbt = new NbtCompound();
        playersWishData.forEach((uuid, playerData) -> {
            NbtCompound playerDataNbt = playerData.generateNbt();
            playersWishDataNbt.put(uuid.toString(), playerDataNbt);
        });
        nbt.put(PLAYERS_WISH_DATA_NBT_KEY, playersWishDataNbt);

        return nbt;
    }

    public static PlayerWishDataManager createFromNbt(NbtCompound tag) {
        PlayerWishDataManager wishDataManager = new PlayerWishDataManager();

        NbtCompound playersWishDataNbt = tag.getCompound(PLAYERS_WISH_DATA_NBT_KEY);
        playersWishDataNbt.getKeys().forEach(uuid -> {
            NbtCompound playerDataNbt = playersWishDataNbt.getCompound(uuid);
            PlayerWishData playerData = PlayerWishData.createFromNbt(playerDataNbt);

            wishDataManager.playersWishData.put(UUID.fromString(uuid), playerData);
        });

        return wishDataManager;
    }

    public static ItemStack rollForPlayer(PlayerEntity player) {
        PlayerWishData playerData = getPlayerData(player);

        playerData.totalWishesMade++;

        if (playerData.totalWishesMade == 1) {
            SatisfyingWeapons.LOGGER.info(player.getName().getString() + " makes their first wish");
            return new ItemStack(ModItems.FIREWORK_SWORD.get(0));
        }

        double rareChance = getRareChance(playerData.wishesSinceRareDrop);
        double epicChance = getEpicChance(playerData.wishesSinceEpicDrop);
        double legendaryChance = getLegendaryChance(playerData.wishesSinceLegendaryDrop);

        SatisfyingWeapons.LOGGER.info(player.getName().getString() + " makes a wish");
        SatisfyingWeapons.LOGGER.info("This is their wish no. " + playerData.totalWishesMade);
        SatisfyingWeapons.LOGGER.info("Rare: " + formatPercentage(rareChance) + "; Epic: " + formatPercentage(epicChance) + "; Legendary: " + formatPercentage(legendaryChance));

        playerData.wishesSinceRareDrop++;
        playerData.wishesSinceEpicDrop++;
        playerData.wishesSinceLegendaryDrop++;

        double seed = MathUtils.randomNumber(1.0);
        ItemStack rolledStack;

        SatisfyingWeapons.LOGGER.info("Roll seed: " + seed);

        if (seed < legendaryChance) {
            rolledStack = new ItemStack(ModItems.SWORD_OF_DYING_STAR.get(0));
            playerData.wishesSinceLegendaryDrop = 0;
        }
        else if (seed < epicChance + legendaryChance) {
            rolledStack = new ItemStack(ModItems.TOY_BOW.get(0));
            playerData.wishesSinceEpicDrop = 0;
        }
        else if (seed < rareChance + epicChance + legendaryChance) {
            rolledStack = MathUtils.takeChance(0.5f) ?
                    new ItemStack(ModItems.FIREWORK_SWORD.get(0)) :
                    new ItemStack(ModItems.GLASS_SWORD.get(0));
            playerData.wishesSinceRareDrop = 0;
        }
        else {
            rolledStack = rollRandomChestLoot(player.getWorld());
        }

        SatisfyingWeapons.LOGGER.info("Rolled item: " + Registries.ITEM.getId(rolledStack.getItem()));

        return rolledStack;
    }

    private static double getRareChance(int x) {
        // 0 => 5%     5 => 13%
        // 2 => 5%     6 => 33%
        // 4 => 7%     7 => 100%
        return 0.0505 + 0.000183 * Math.exp(1.223 * x);
    }

    private static double getEpicChance(int x) {
        // 0 => 2%     15 => 12%
        // 5 => 2%     16 => 19%
        // 10 => 3%    17 => 33%
        // 12 => 4%    18 => 57%
        // 14 => 7%    19 => 100%
        return 0.0244 + 0.0000116 * Math.exp(0.598 * x);
    }

    private static double getLegendaryChance(int x) {
        // 0 => 12%     25 => 8%     47 => 55%
        // 1 => 7%      30 => 4%     48 => 68%
        // 5 => 1%      35 => 1%     49 => 84%
        // 10 => 1%     40 => 6%     50 => 100%
        // 15 => 4%     43 => 19%
        // 20 => 9%     45 => 34%
        return Math.max(0.01, 0.00000271503 * Math.pow(x, 4) - 0.000233831 * Math.pow(x, 3) + 0.00643612  * Math.pow(x, 2) - 0.0588076 * x + 0.124785);
    }

    private static ItemStack rollRandomChestLoot(World world) {
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

    private static String formatPercentage(double x) {
        return MathHelper.clamp(Math.round(x * 10000) / 100.0, 0, 100) + "%";
    }

    private static PlayerWishDataManager getServerInstance(MinecraftServer server) {
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;
        PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();

        PlayerWishDataManager wishDataManager = persistentStateManager.getOrCreate(
                PlayerWishDataManager::createFromNbt,
                PlayerWishDataManager::new,
                SatisfyingWeapons.MOD_ID
        );

        wishDataManager.markDirty();
        return wishDataManager;
    }

    private static PlayerWishData getPlayerData(PlayerEntity player) {
        MinecraftServer server = player.getWorld().getServer();
        assert server != null;
        PlayerWishDataManager wishDataManager = getServerInstance(server);

        return wishDataManager.playersWishData.computeIfAbsent(player.getUuid(), uuid -> new PlayerWishData());
    }
}
