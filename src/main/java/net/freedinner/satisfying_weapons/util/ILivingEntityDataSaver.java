package net.freedinner.satisfying_weapons.util;

public interface ILivingEntityDataSaver {
    int sw$getGlassCutCountdown();
    void sw$setGlassCutCountdown(int ticks);

    int sw$getDropAttemptsBH();
    void sw$setDropAttemptsBH(int amount);
}
