package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.freedinner.satisfying_weapons.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IUpgradeableWeapon {
    ModToolMaterial getRarityMaterial();

    int getLevel();

    @Nullable IUpgradeableWeapon getNextLevel();

    default int getMaxLevel() {
        IUpgradeableWeapon weapon = this;

        while (weapon.getNextLevel() != null) {
            weapon = weapon.getNextLevel();
        }

        return weapon.getLevel();
    }

    default boolean isAtMaxLevel() {
        return this.getNextLevel() == null;
    }
    
    default List<Text> getCollapsedDescription() {
        ArrayList<Text> desc = new ArrayList<>();
        desc.add(Text.literal("Hold Shift for more info").formatted(Formatting.YELLOW));

        return desc;
    }

    default List<Text> getSimpleDescription(ItemStack stack) {
        ArrayList<Text> desc = new ArrayList<>();

        MutableText levelText = Text.literal("Level " + this.getLevel()).formatted(Formatting.YELLOW);

        MutableText rarityText = switch (this.getRarityMaterial()) {
            case RARE -> Text.literal("★☆☆ Rare").formatted(Formatting.GREEN);
            case EPIC -> Text.literal("★★☆ Epic").formatted(Formatting.AQUA);
            case LEGENDARY -> Text.literal("★★★ Legendary").setStyle(Style.EMPTY.withColor(-14336));
        };

        levelText.append("   ").append(rarityText);

        desc.add(levelText);
        desc.add(Text.empty());

        MutableText description = switch (this.getLevel()) {
            case 1 ->
                    Text.literal("Hitting a mob grants 1_Festivity, up to 5 stacks. While in the air, press jump to consume 3_Festivity and do a Firework Jump. During a Firework Jump, sneak to do a plunge attack.");
            case 2 ->
                    Text.literal("Plunge attack damage increases by_25% and is further increased by_15% for each mob in its radius. Max damage increase is_100%.");
            case 3 ->
                    Text.literal("For each mob damaged by a plunge attack, recover 1_Festivity. Max 2_stacks per plunge attack.");
            case 4 -> Text.literal("For each mob damaged by a plunge attack, recover 1_HP.");
            case 5 ->
                    Text.literal("Festivity can now go up to 10_stacks. Also, Festivity recovered by Level_3 is no longer limited to 2_stacks per plunge.");
            default -> Text.empty();
        };

        TextUtils.addLongTooltip(desc, description, Formatting.GRAY);

        if (stack.hasEnchantments()) {
            desc.add(Text.empty());
        }

        return desc;
    }

    default List<Text> getLeveledDescription(ItemStack stack) {
        ArrayList<Text> desc = new ArrayList<>();

        NbtCompound stackNbt = stack.getOrCreateNbt();

        long lastViewedTime = NbtUtils.getOrCreate(stackNbt, "sw_last_viewed_time", 0);
        boolean shouldReset = MinecraftClient.getInstance().world.getTime() - lastViewedTime > 5;

        if (shouldReset) {
            stackNbt.putInt("sw_curr_page", this.getLevel());
            stackNbt.putBoolean("sw_ctrl_held", true);
            stackNbt.putBoolean("sw_alt_held", true);
        }

        stackNbt.putLong("sw_last_viewed_time", MinecraftClient.getInstance().world.getTime());

        int currPage = NbtUtils.getOrCreate(stackNbt, "sw_curr_page", this.getLevel());
        boolean ctrlHeld = NbtUtils.getOrCreate(stackNbt, "sw_ctrl_held", true);
        boolean altHeld = NbtUtils.getOrCreate(stackNbt, "sw_alt_held", true);

        if (Screen.hasControlDown() && !ctrlHeld) {
            ctrlHeld = true;
            currPage = Math.max(1, currPage - 1);
        }

        if (!Screen.hasControlDown() && ctrlHeld) {
            ctrlHeld = false;
        }

        if (Screen.hasAltDown() && !altHeld) {
            altHeld = true;
            currPage = Math.min(this.getMaxLevel(), currPage + 1);
        }

        if (!Screen.hasAltDown() && altHeld) {
            altHeld = false;
        }

        stackNbt.putInt("sw_curr_page", currPage);
        stackNbt.putBoolean("sw_ctrl_held", ctrlHeld);
        stackNbt.putBoolean("sw_alt_held", altHeld);

        MutableText levelText = Text.literal("Level " + currPage).formatted(Formatting.YELLOW);

        if (currPage != this.getLevel()) {
            levelText.append(Text.literal(" (Preview)").formatted(Formatting.GRAY));
        }

        MutableText rarityText = switch (this.getRarityMaterial()) {
            case RARE -> Text.literal("★☆☆ Rare").formatted(Formatting.GREEN);
            case EPIC -> Text.literal("★★☆ Epic").formatted(Formatting.AQUA);
            case LEGENDARY -> Text.literal("★★★ Legendary").setStyle(Style.EMPTY.withColor(-14336));
        };

        levelText.append("   ").append(rarityText);

        desc.add(levelText);
        desc.add(Text.empty());

        MutableText description = switch (currPage) {
            case 1 ->
                    Text.literal("Hitting a mob grants 1_Festivity, up to 5 stacks. While in the air, press jump to consume 3_Festivity and do a Firework Jump. During a Firework Jump, sneak to do a plunge attack.");
            case 2 ->
                    Text.literal("Plunge attack damage increases by_25% and is further increased by_15% for each mob in its radius. Max damage increase is_100%.");
            case 3 ->
                    Text.literal("For each mob damaged by a plunge attack, recover 1_Festivity. Max 2_stacks per plunge attack.");
            case 4 -> Text.literal("For each mob damaged by a plunge attack, recover 1_HP.");
            case 5 ->
                    Text.literal("Festivity can now go up to 10_stacks. Also, Festivity recovered by Level_3 is no longer limited to 2_stacks per plunge.");
            default -> Text.empty();
        };

        TextUtils.addLongTooltip(desc, description, Formatting.GRAY);

        MutableText levelSelection = Text.literal("←- ctrl");
        levelSelection.append(" ".repeat(TextUtils.MAX_LINE_LENGTH / 2 - 5 - this.getMaxLevel()));
        levelSelection.append("•").formatted(Formatting.GRAY);

        for (int i = 1; i <= this.getMaxLevel(); i++) {
            if (currPage == i) {
                levelSelection.append(Text.literal("" + i).formatted(Formatting.WHITE, Formatting.BOLD));
            }
            else {
                levelSelection.append(Text.literal("" + i).formatted(Formatting.GRAY));
            }

            levelSelection.append("•").formatted(Formatting.GRAY);
        }

        levelSelection.append(" ".repeat(TextUtils.MAX_LINE_LENGTH / 2 - 5 - this.getMaxLevel()));
        levelSelection.append("alt -→");

        desc.add(Text.empty());
        desc.add(levelSelection);

        if (stack.hasEnchantments()) {
            desc.add(Text.empty());
        }

        return desc;
    }
}
