package net.freedinner.satisfying_weapons;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class PlayerWishDataSaver extends PersistentState {
    public int totalWishesMade = 0;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("total_wishes_made", totalWishesMade);
        return nbt;
    }

    public static PlayerWishDataSaver createFromNbt(NbtCompound tag) {
        PlayerWishDataSaver wishDataSaver = new PlayerWishDataSaver();
        wishDataSaver.totalWishesMade = tag.getInt("total_wishes_made");
        return wishDataSaver;
    }

    public static PlayerWishDataSaver createNew() {
        PlayerWishDataSaver wishDataSaver = new PlayerWishDataSaver();
        wishDataSaver.totalWishesMade = 0;
        return wishDataSaver;
    }

    public static PlayerWishDataSaver getServerState(MinecraftServer server) {
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;
        PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();

        PlayerWishDataSaver wishDataSaver = persistentStateManager.getOrCreate(
                PlayerWishDataSaver::createFromNbt,
                PlayerWishDataSaver::createNew,
                SatisfyingWeapons.MOD_ID
        );

        wishDataSaver.markDirty();
        return wishDataSaver;
    }
}
