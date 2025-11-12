package net.freedinner.satisfying_weapons.util.data;

public interface ILivingEntityDataSaver {
    int sw$getGlassCutCountdown();
    void sw$setGlassCutCountdown(int ticks);

    int sw$getBrokenSoulSwordLevel();
    void sw$setBrokenSoulSwordLevel(int level);

    int sw$getDropAttemptsBH();
    void sw$setDropAttemptsBH(int amount);
}
