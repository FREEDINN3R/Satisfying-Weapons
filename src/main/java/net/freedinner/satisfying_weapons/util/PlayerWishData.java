package net.freedinner.satisfying_weapons.util;

import net.minecraft.nbt.NbtCompound;

public class PlayerWishData {
    public int totalWishesMade = 0;
    public int wishesSinceRareDrop = 0;
    public int wishesSinceEpicDrop = 0;
    public int wishesSinceLegendaryDrop = 0;

    private static final String TOTAL_WISHES_MADE_NBT_KEY = "total_wishes_made";
    private static final String WISHES_SINCE_RARE_DROP_NBT_KEY = "wishes_since_rare_drop";
    private static final String WISHES_SINCE_EPIC_DROP_NBT_KEY = "wishes_since_epic_drop";
    private static final String WISHES_SINCE_LEGENDARY_DROP_NBT_KEY = "wishes_since_legendary_drop";

    public NbtCompound generateNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putInt(TOTAL_WISHES_MADE_NBT_KEY, totalWishesMade);
        nbt.putInt(WISHES_SINCE_RARE_DROP_NBT_KEY, wishesSinceRareDrop);
        nbt.putInt(WISHES_SINCE_EPIC_DROP_NBT_KEY, wishesSinceEpicDrop);
        nbt.putInt(WISHES_SINCE_LEGENDARY_DROP_NBT_KEY, wishesSinceLegendaryDrop);

        return nbt;
    }

    public static PlayerWishData createFromNbt(NbtCompound nbt) {
        PlayerWishData playerData = new PlayerWishData();

        playerData.totalWishesMade = nbt.getInt(TOTAL_WISHES_MADE_NBT_KEY);
        playerData.wishesSinceRareDrop = nbt.getInt(WISHES_SINCE_RARE_DROP_NBT_KEY);
        playerData.wishesSinceEpicDrop = nbt.getInt(WISHES_SINCE_EPIC_DROP_NBT_KEY);
        playerData.wishesSinceLegendaryDrop = nbt.getInt(WISHES_SINCE_LEGENDARY_DROP_NBT_KEY);

        return playerData;
    }
}
