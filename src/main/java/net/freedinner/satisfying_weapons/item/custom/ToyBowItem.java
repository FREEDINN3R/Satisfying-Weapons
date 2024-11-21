package net.freedinner.satisfying_weapons.item.custom;

import net.minecraft.item.ToolMaterial;
import org.jetbrains.annotations.Nullable;

public class ToyBowItem extends UpgradeableBowItem {
    public ToyBowItem(ToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableBowItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }
}
