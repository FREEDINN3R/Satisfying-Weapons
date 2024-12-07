package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class UpgradeableSwordItem extends SwordItem implements IUpgradeableWeapon {
    private final int level;
    @Nullable
    protected final UpgradeableSwordItem nextLevelWeapon;

    public UpgradeableSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableSwordItem nextLevelWeapon) {
        super(toolMaterial, 3, -2.4f, settings);

        this.level = level;
        this.nextLevelWeapon = nextLevelWeapon;
    }

    @Override
    public ModToolMaterial getRarityMaterial() {
        return (ModToolMaterial) this.getMaterial();
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
        tooltip.addAll(this.generateWeaponDescription(stack, world));
    }
}
