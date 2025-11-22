package net.freedinner.satisfying_weapons.util.data;

public interface
IPlayerDataSaver {
    long sw$getLastDropTime();
    void sw$setLastDropTime(long worldTick);

    int sw$getOnGroundTimeFS();
    void sw$setOnGroundTimeFS(int time);

    int sw$getChargeMS();
    void sw$setChargeMS(int i);
}
