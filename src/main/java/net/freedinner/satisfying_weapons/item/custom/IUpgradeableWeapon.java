package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.NbtUtils;
import net.freedinner.satisfying_weapons.util.TextUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IUpgradeableWeapon {
    String LAST_VIEWED_TIME_NBT_KEY = "sw_last_viewed_time";
    String CURR_PAGE_NBT_KEY = "sw_curr_page";
    String CTRL_WAS_HELD_NBT_KEY = "sw_ctrl_was_held";
    String ALT_WAS_HELD_NBT_KEY = "sw_alt_was_held";

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

    default List<Text> generateWeaponDescription(ItemStack stack, @Nullable World world) {
        List<Text> weaponDescription;

        if (!Screen.hasShiftDown()) {
            // Just says "Hold Shift"
            weaponDescription = this.getCollapsedDescription();
        }
        else {
            // World is needed because multipage description uses world ticks
            weaponDescription = (world != null) ? getMultipageDescription(stack, world) : getSimpleDescription(stack);
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
        int currLevel = this.getLevel();

        // Weapon rarity text, goes after level
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
                        .append(" " + currLevel)
                        .formatted(Formatting.YELLOW)
                        .append("   ")
                        .append(rarityText)
        );

        // Spacing
        desc.add(Text.empty());

        // Skill name text
        String weaponName = stack.getItem().getTranslationKey();
        Text skillName = Text.translatable(weaponName + ".skill_name_" + currLevel)
                .formatted(Formatting.WHITE, Formatting.ITALIC);
        desc.add(skillName);

        // Skill description text
        Text skillDesc = Text.translatable(weaponName + ".skill_desc_" + currLevel);
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

    default List<Text> getMultipageDescription(ItemStack stack, @NotNull World world) {
        ArrayList<Text> desc = new ArrayList<>();
        int currLevel = this.getLevel();
        NbtCompound stackNbt = stack.getOrCreateNbt();

        // Last tick when description of this weapon was viewed
        long lastViewedTime = NbtUtils.getOrCreate(stackNbt, LAST_VIEWED_TIME_NBT_KEY, 0);
        long currTime = world.getTime();

        // If the player wasn't viewing the full description continuously
        if (currTime - lastViewedTime > 5) {
            // Reset everything
            stackNbt.putInt(CURR_PAGE_NBT_KEY, currLevel);
            stackNbt.putBoolean(CTRL_WAS_HELD_NBT_KEY, true);
            stackNbt.putBoolean(ALT_WAS_HELD_NBT_KEY, true); // Both true, to prevent players from flipping a page immediately
        }

        // After all relevant checks, record "last viewed" tick
        stackNbt.putLong(LAST_VIEWED_TIME_NBT_KEY, currTime);

        // Oh boy here we go
        int currPage = NbtUtils.getOrCreate(stackNbt, CURR_PAGE_NBT_KEY, currLevel);
        boolean ctrlWasHeld = NbtUtils.getOrCreate(stackNbt, CTRL_WAS_HELD_NBT_KEY, true);
        boolean altWasHeld = NbtUtils.getOrCreate(stackNbt, ALT_WAS_HELD_NBT_KEY, true);

        // If player pressed Ctrl
        if (Screen.hasControlDown() && !ctrlWasHeld) {
            ctrlWasHeld = true;
            currPage = Math.max(1, currPage - 1); // Go back a page
        }

        // If player released Ctrl
        if (!Screen.hasControlDown() && ctrlWasHeld) {
            ctrlWasHeld = false;
        }

        // If player pressed Alt
        if (Screen.hasAltDown() && !altWasHeld) {
            altWasHeld = true;
            currPage = Math.min(this.getMaxLevel(), currPage + 1); // Go forward a page
        }

        // If player released Alt
        if (!Screen.hasAltDown() && altWasHeld) {
            altWasHeld = false;
        }

        // After all relevant checks, record current page and key states
        stackNbt.putInt(CURR_PAGE_NBT_KEY, currPage);
        stackNbt.putBoolean(CTRL_WAS_HELD_NBT_KEY, ctrlWasHeld);
        stackNbt.putBoolean(ALT_WAS_HELD_NBT_KEY, altWasHeld);

        // Weapon rarity text, goes after level
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
                        .append(" " + currPage)
                        .formatted((currPage <= currLevel) ? Formatting.YELLOW : Formatting.GRAY)
                        .append("   ")
                        .append(rarityText)
        );

        // Spacing
        desc.add(Text.empty());

        // Skill name text
        String weaponName = stack.getItem().getTranslationKey();
        MutableText skillName = Text.translatable(weaponName + ".skill_name_" + currPage)
                .formatted(Formatting.WHITE, Formatting.ITALIC);

        // If viewing the next level, add (Preview)
        if (currLevel + 1 == currPage) {
            skillName.append(Text.empty()
                    .append(" (")
                    .append(Text.translatable("item.satisfying_weapons.desc.preview"))
                    .append(")")
                    .formatted(Formatting.GRAY)
            );
        }

        desc.add(skillName);

        // Skill description text
        if (currPage <= currLevel + 1) {
            Text skillDesc = Text.translatable(weaponName + ".skill_desc_" + currPage);
            List<MutableText> skillDescLines = TextUtils.breakDownLongTooltip(skillDesc)
                    .stream()
                    .map(text -> text.formatted(Formatting.GRAY))
                    .toList();
            desc.addAll(skillDescLines);
        }
        else {
            // Skill description is not available more than 1 level ahead
            desc.add(Text.literal("???").formatted(Formatting.GRAY));
        }

        // Spacing
        desc.add(Text.empty());

        // Unlock criteria
        if (currPage > currLevel) {
            Text unlockCriteria = Text.translatable("item.satisfying_weapons.desc.unlock_criteria");
            List<MutableText> unlockCriteriaLines = TextUtils.breakDownLongTooltip(unlockCriteria)
                    .stream()
                    .map(text -> text.formatted(Formatting.UNDERLINE))
                    .toList();
            desc.addAll(unlockCriteriaLines);

            // Spacing
            desc.add(Text.empty());
        }

        // Level selection text, starts left arrow and spacing
        MutableText levelSelection = Text.empty()
                .append("←- ctrl")
                .append(" ".repeat(TextUtils.MAX_LINE_LENGTH / 2 - 5 - this.getMaxLevel()))
                .append("•")
                .formatted(Formatting.GRAY);

        // Row of page numbers
        for (int i = 1; i <= this.getMaxLevel(); i++) {
            MutableText pageNumber = Text.literal("" + i).formatted(Formatting.GRAY);

            // The selected page is highlighted
            if (currPage == i) {
                pageNumber.formatted(Formatting.WHITE, Formatting.BOLD);
            }

            levelSelection
                    .append(pageNumber)
                    .append(Text.literal("•").formatted(Formatting.GRAY));
        }

        // Right spacing and arrow
        levelSelection
                .append(" ".repeat(TextUtils.MAX_LINE_LENGTH / 2 - 5 - this.getMaxLevel()))
                .append("alt -→");

        desc.add(levelSelection);

        // Extra spacing before enchantments
        if (stack.hasEnchantments()) {
            desc.add(Text.empty());
        }

        return desc;
    }
}
