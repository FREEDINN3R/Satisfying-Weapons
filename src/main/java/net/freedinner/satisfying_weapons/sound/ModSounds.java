package net.freedinner.satisfying_weapons.sound;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static SoundEvent PLUNGE_ATTACK = register("plunge_attack");
    public static SoundEvent GLASS_CUT_DAMAGE = register("glass_cut_damage");
    public static SoundEvent PARTY_HORN = register("party_horn");
    public static SoundEvent BIRTHDAY_GIFT_PRIMED = register("birthday_gift_primed");
    public static SoundEvent BIRTHDAY_GIFT_EXPLOSION = register("birthday_gift_explosion");
    public static SoundEvent BLACK_HOLE_THROWN = register("black_hole_thrown");
    public static SoundEvent BLACK_HOLE_ACTIVE = register("black_hole_active");

    private static SoundEvent register(String name) {
        Identifier id = SatisfyingWeapons.id(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        SatisfyingWeapons.LOGGER.info("Registering sounds");
    }
}
