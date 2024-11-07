package net.freedinner.satisfying_weapons.item.custom;

import org.jetbrains.annotations.Nullable;

public interface IUpgradeableWeapon {
    int getLevel();

    default boolean isAtMaxLevel() {
        return this.getNextLevel() == null;
    }

    @Nullable IUpgradeableWeapon getNextLevel();
}
