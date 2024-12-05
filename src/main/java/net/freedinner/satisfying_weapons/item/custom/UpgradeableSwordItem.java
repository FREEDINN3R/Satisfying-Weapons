package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.TextUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class UpgradeableSwordItem extends SwordItem implements IUpgradeableWeapon {
    private final int level;
    @Nullable
    protected final UpgradeableSwordItem nextLevelWeapon;

    public UpgradeableSwordItem(ToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableSwordItem nextLevelWeapon) {
        super(toolMaterial, 3, -2.4f, settings);

        this.level = level;
        this.nextLevelWeapon = nextLevelWeapon;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public @Nullable IUpgradeableWeapon getNextLevel() {
        return nextLevelWeapon;
    }

    @Override
    public String getTranslationKey() {
        String translationKey = super.getTranslationKey();
        return translationKey.substring(0, translationKey.length() - 3);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if (Screen.hasShiftDown()) {
            MutableText levelText = Text.literal("Level " + this.getLevel()).formatted(Formatting.YELLOW);

            if (this.getMaterial() instanceof ModToolMaterial toolMaterial) {
                MutableText rarityText = switch (toolMaterial) {
                    case RARE -> Text.literal("★☆☆ Rare").formatted(Formatting.GREEN);
                    case EPIC -> Text.literal("★★☆ Epic").formatted(Formatting.AQUA);
                    case LEGENDARY -> Text.literal("★★★ Legendary").setStyle(Style.EMPTY.withColor(-14336));
                };

                levelText.append("   ").append(rarityText);
            }

            tooltip.add(levelText);
            tooltip.add(Text.empty());

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

            TextUtils.addLongTooltip(tooltip, description, Formatting.GRAY);

            MutableText levelSelection = Text.literal("←- a");
            levelSelection.append(" ".repeat(TextUtils.MAX_LINE_LENGTH / 2 - 3 - this.getMaxLevel()));
            levelSelection.append("•").formatted(Formatting.GRAY);

            for (int i = 1; i <= this.getMaxLevel(); i++) {
                if (this.getLevel() == i) {
                    levelSelection.append(Text.literal("" + i).formatted(Formatting.WHITE, Formatting.BOLD));
                }
                else {
                    levelSelection.append(Text.literal("" + i).formatted(Formatting.GRAY));
                }

                levelSelection.append("•").formatted(Formatting.GRAY);
            }

            levelSelection.append(" ".repeat(TextUtils.MAX_LINE_LENGTH / 2 - 3 - this.getMaxLevel()));
            levelSelection.append("d -→");

            tooltip.add(Text.empty());
            tooltip.add(levelSelection);

            if (stack.hasEnchantments()) {
                tooltip.add(Text.empty());
            }
        }
        else {
            tooltip.add(Text.literal("Hold Shift for more info").formatted(Formatting.YELLOW));
        }
    }
}
