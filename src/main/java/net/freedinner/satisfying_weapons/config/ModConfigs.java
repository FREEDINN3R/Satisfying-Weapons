package net.freedinner.satisfying_weapons.config;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class ModConfigs {
    public static SimpleConfig CONFIG;

    public static String BLACK_HOLE_IGNORE_PETS;
    private static final String BLACK_HOLE_IGNORE_PETS_KEY = "black_hole_ignore_pets";
    private static final String BLACK_HOLE_IGNORE_PETS_DEFAULT = "only_your_own";

    public static ArrayList<String> BLACK_HOLE_IGNORED_ENTITIES;
    private static final String BLACK_HOLE_IGNORED_ENTITIES_KEY = "black_hole_ignored_entities";
    private static final String BLACK_HOLE_IGNORED_ENTITIES_DEFAULT = "[ 'minecraft:armor_stand', 'some_mod:another_entity' ]";

    public static void registerConfigs() {
        SatisfyingWeapons.LOGGER.info("Registering configs");

        ModConfigProvider configProvider = createConfigProvider();
        CONFIG = SimpleConfig.of(SatisfyingWeapons.MOD_ID + "_config").provider(configProvider).request();

        assignConfigs();
    }

    private static ModConfigProvider createConfigProvider() {
        ModConfigProvider configProvider = new ModConfigProvider();

        configProvider.addComment("Whether Black Holes should ignore all pets, attract all pets, or ignore only your own pets");
        configProvider.addComment("May not work perfectly with pets from other mods");
        configProvider.addComment("Values: yes / no / only_your_own");
        configProvider.addField(BLACK_HOLE_IGNORE_PETS_KEY, BLACK_HOLE_IGNORE_PETS_DEFAULT);
        configProvider.addComment("");

        configProvider.addComment("Entities that should be completely ignored by Black Holes");
        configProvider.addField(BLACK_HOLE_IGNORED_ENTITIES_KEY, BLACK_HOLE_IGNORED_ENTITIES_DEFAULT);

        return configProvider;
    }

    private static void assignConfigs() {
        BLACK_HOLE_IGNORE_PETS = CONFIG.getOrDefault(BLACK_HOLE_IGNORE_PETS_KEY, BLACK_HOLE_IGNORE_PETS_DEFAULT);
        BLACK_HOLE_IGNORED_ENTITIES = configArrayToList(
                CONFIG.getOrDefault(BLACK_HOLE_IGNORED_ENTITIES_KEY, BLACK_HOLE_IGNORED_ENTITIES_DEFAULT)
        );
    }

    private static ArrayList<String> configArrayToList(String s) {
        ArrayList<String> list = new ArrayList<>();

        int searchPos = 0;
        while (searchPos < s.length() && s.indexOf("'", searchPos) != -1) {
            int startPos = s.indexOf("'", searchPos) + 1;
            int endPos = s.indexOf("'", startPos);

            if (endPos == -1) {
                break;
            }

            list.add(s.substring(startPos, endPos));
            searchPos = endPos + 1;
        }

        return list;
    }
}
