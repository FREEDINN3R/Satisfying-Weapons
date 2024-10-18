package net.freedinner.satisfying_weapons.sound;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static SoundEvent PLUNGE_ATTACK = register("plunge_attack");

    private static SoundEvent register(String name) {
        Identifier id = SatisfyingWeapons.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        SatisfyingWeapons.LOGGER.info("Registering sounds");
    }
}
