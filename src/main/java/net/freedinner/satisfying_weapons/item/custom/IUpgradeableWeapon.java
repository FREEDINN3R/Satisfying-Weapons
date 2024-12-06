package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.freedinner.satisfying_weapons.util.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IUpgradeableWeapon {
    String LAST_VIEWED_TIME_NBT_KEY = "sw_last_viewed_time";
    String CURR_PAGE_NBT_KEY = "sw_curr_page";
    String CTRL_HELD_NBT_KEY = "sw_ctrl_held";
    String ALT_HELD_NBT_KEY = "sw_alt_held";

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

    default List<Text> generateWeaponDescription(ItemStack stack) {
        List<Text> weaponDescription;

        if (!Screen.hasShiftDown()) {
            // Just says "Hold Shift"
            weaponDescription = this.getCollapsedDescription();
        }
        else {
            // Client world is needed because multipage description uses world ticks
            World clientWorld = null;
            weaponDescription = (clientWorld != null) ? getLeveledDescription(stack) : getSimpleDescription(stack);
        }

        return weaponDescription;
    }
    
    default List<Text> getCollapsedDescription() {
        ArrayList<Text> desc = new ArrayList<>();
        desc.add(Text.translatable("item.satisfying_weapons.desc.hold_shift").formatted(Formatting.YELLOW));

        return desc;
    }

    default List<Text> getSimpleDescription(ItemStack stack) {
        ArrayList<Text> desc = new ArrayList<>();

        // Weapon rarity text
        MutableText rarityText = switch (this.getRarityMaterial()) {
            case RARE -> Text.literal("★☆☆ ")
                    .append(Text.translatable("item.satisfying_weapons.desc.rare"))
                    .formatted(Formatting.GREEN);
            case EPIC -> Text.literal("★★☆ ")
                    .append(Text.translatable("item.satisfying_weapons.desc.epic"))
                    .formatted(Formatting.LIGHT_PURPLE);
            case LEGENDARY -> Text.literal("★★★ ")
                    .append(Text.translatable("item.satisfying_weapons.desc.legendary"))
                    .setStyle(Style.EMPTY.withColor(-14336));
        };

        // Weapon level text
        desc.add(
                Text.translatable("item.satisfying_weapons.desc.level")
                        .append(" " + this.getLevel())
                        .formatted(Formatting.YELLOW)
                        .append("   ")
                        .append(rarityText)
        );

        // Spacing
        desc.add(Text.empty());

        // Skill name text
        String weaponName = stack.getItem().getTranslationKey();
        Text skillName = Text.translatable(weaponName + ".skill_name_" + this.getLevel())
                .formatted(Formatting.WHITE, Formatting.ITALIC);
        desc.add(skillName);

        // Skill description text
        Text skillDesc = Text.translatable(weaponName + ".skill_desc_" + this.getLevel());
        List<MutableText> skillDescLines = TextUtils.breakDownLongTooltip(skillDesc)
                .stream()
                .map(text -> text.formatted(Formatting.GRAY))
                .toList();
        desc.addAll(skillDescLines);

        // Extra spacing before enchantments
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

        //TextUtils.addLongTooltip(desc, description, Formatting.GRAY);

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
