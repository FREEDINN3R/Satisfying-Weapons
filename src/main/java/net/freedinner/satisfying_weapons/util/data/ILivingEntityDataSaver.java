package net.freedinner.satisfying_weapons.util.data;

import net.freedinner.satisfying_weapons.item.custom.CrimsonKatanaItem;

import java.util.List;

public interface ILivingEntityDataSaver {
    int sw$getGlassCutCountdown();
    void sw$setGlassCutCountdown(int ticks);

    int sw$getBrokenSoulSwordLevel();
    void sw$setBrokenSoulSwordLevel(int level);

    void sw$scheduleDots(List<CrimsonKatanaItem.DoT> scheduledDots);
    void sw$cancelNextDots();

    int sw$getDropAttemptsBH();
    void sw$setDropAttemptsBH(int amount);
}
