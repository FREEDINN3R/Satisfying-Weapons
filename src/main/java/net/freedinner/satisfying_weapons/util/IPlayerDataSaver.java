package net.freedinner.satisfying_weapons.util;

public interface IPlayerDataSaver {
    long getLastDropTime();
    void setLastDropTime(long worldTick);
}
