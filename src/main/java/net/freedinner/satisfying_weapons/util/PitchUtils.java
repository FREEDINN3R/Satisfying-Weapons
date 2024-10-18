package net.freedinner.satisfying_weapons.util;

import java.util.Random;

public class PitchUtils {
    private static final Random random = new Random();

    public static float get() {
        return get(0.1f);
    }

    public static float get(float d) {
        return 1 - d + random.nextFloat(2 * d);
    }
}
