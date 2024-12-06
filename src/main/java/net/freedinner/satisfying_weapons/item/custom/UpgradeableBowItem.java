package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class UpgradeableBowItem extends BowItem implements IUpgradeableWeapon {
    private final ModToolMaterial rarityMaterial;
    private final int level;
    @Nullable
    protected final UpgradeableBowItem nextLevelWeapon;

    public UpgradeableBowItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableBowItem nextLevelWeapon) {
        super(settings.maxDamage(toolMaterial.getDurability()));

        this.rarityMaterial = toolMaterial;
        this.level = level;
        this.nextLevelWeapon = nextLevelWeapon;
    }

    @Override
    public ModToolMaterial getRarityMaterial() {
        return rarityMaterial;
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
        tooltip.addAll(this.generateWeaponDescription(stack));
    }
}
