package net.freedinner.satisfying_weapons.item.custom;

import org.jetbrains.annotations.Nullable;

public interface IUpgradeableWeapon {
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
}
