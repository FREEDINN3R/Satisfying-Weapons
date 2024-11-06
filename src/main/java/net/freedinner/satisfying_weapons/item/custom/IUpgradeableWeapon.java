package net.freedinner.satisfying_weapons.item.custom;

public interface IUpgradeableWeapon {
    int getLevel();

    default int getMaxLevel() {
        return 5;
    }
}
