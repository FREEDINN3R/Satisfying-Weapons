package net.freedinner.satisfying_weapons.util;

public class PitchUtils {
    public static float get() {
        return get(0.1f);
    }

    public static float get(float d) {
        return 1 - d + MathUtils.randomNumber(2 * d);
    }
}
