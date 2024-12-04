package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
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

        MutableText levelText = Text.literal("Level " + this.getLevel()).formatted(Formatting.YELLOW);

        SatisfyingWeapons.LOGGER.info(Color.orange.getRGB() + "");

        if (this.getMaterial() instanceof ModToolMaterial toolMaterial) {
            MutableText rarityText = switch (toolMaterial) {
                case RARE -> Text.literal("★☆☆").formatted(Formatting.GREEN);
                case EPIC -> Text.literal("★★☆").formatted(Formatting.AQUA);
                case LEGENDARY -> Text.literal("★★★").setStyle(Style.EMPTY.withColor(Color.orange.getRGB()));
            };

            levelText.append("  ").append(rarityText);
        }

        tooltip.add(levelText);
        tooltip.add(Text.empty());
    }
}
