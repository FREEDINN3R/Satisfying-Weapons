package net.freedinner.satisfying_weapons.util;

public interface
IPlayerDataSaver {
    long satisfyingWeapons$getLastDropTime();
    void satisfyingWeapons$setLastDropTime(long worldTick);

    int satisfyingWeapons$getOnGroundTimeFS();

    void satisfyingWeapons$setOnGroundTimeFS(int time);
}
