package net.freedinner.satisfying_weapons.effect;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.effect.custom.BirthdayPartyEffect;
import net.freedinner.satisfying_weapons.effect.custom.EntropyEffect;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.freedinner.satisfying_weapons.effect.custom.FireworkJumpEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.awt.*;
import java.util.function.BiFunction;

public class ModEffects {
    public static StatusEffect FESTIVITY = registerStatusEffect("festivity",
            new Color(255, 255, 255),
            StatusEffectCategory.NEUTRAL,
            FestivityEffect::new);
    public static StatusEffect FIREWORK_JUMP = registerStatusEffect("firework_jump",
            new Color(255, 255, 255),
            StatusEffectCategory.NEUTRAL,
            FireworkJumpEffect::new);
    public static StatusEffect BIRTHDAY_PARTY = registerStatusEffect("birthday_party",
            new Color(241, 35, 222),
            StatusEffectCategory.HARMFUL,
            BirthdayPartyEffect::new);
    public static StatusEffect ENTROPY = registerStatusEffect("entropy",
            new Color(108, 63, 224),
            StatusEffectCategory.HARMFUL,
            EntropyEffect::new);

    private static StatusEffect registerStatusEffect(
            String name, Color color, StatusEffectCategory category,
            BiFunction<StatusEffectCategory, Integer, StatusEffect> constructor) {
        return Registry.register(
                Registries.STATUS_EFFECT, SatisfyingWeapons.id(name),
                constructor.apply(category, color.getRGB())
        );
    }

    public static void registerEffects() {
        SatisfyingWeapons.LOGGER.info("Registering effects");
    }
}
