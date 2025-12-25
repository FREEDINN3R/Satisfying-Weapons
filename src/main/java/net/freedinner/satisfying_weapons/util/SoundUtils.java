package net.freedinner.satisfying_weapons.util;

public class SoundUtils {
    public static float getPitch() {
        return getPitch(0.1f);
    }

    public static float getPitch(float d) {
        return 1 - d + MathUtils.randomNumber(2 * d);
    }
}
