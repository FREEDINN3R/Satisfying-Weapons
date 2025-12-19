package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrimsonKatanaItem extends UpgradeableSwordItem {
    public CrimsonKatanaItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable CrimsonKatanaItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDead()) {
            return super.postHit(stack, target, attacker);
        }

        List<DoT> existingDots = DoT.getDotsOn(target, this.getLevel());
        float dotMultiplier = (this.getLevel() >= 2 && existingDots.size() >= 2) ? 1.2f : 1.0f;

        for (DoT dotEffect : existingDots) {
            dotEffect.detonateFor(target, dotMultiplier);
        }

        return super.postHit(stack, target, attacker);
    }

    public enum DoT {
        // Poison has to go first since it's non-lethal
        POISON(2, StatusEffects.POISON, ModDamageTypes.INSTANT_POISON, new int[] {25, 12, 6, 3, 1}),
        WITHER(1, StatusEffects.WITHER, ModDamageTypes.INSTANT_WITHER, new int[] {40, 20, 10, 5, 2, 1}),
        BURN(4, null, ModDamageTypes.INSTANT_BURN, new int[] {20});

        private final int levelRequired;
        private final StatusEffect baseEffect;
        private final RegistryKey<DamageType> damageType;
        private final int[] dmgRate;

        DoT(int levelRequired, StatusEffect baseEffect, RegistryKey<DamageType> damageType, int[] dmgRate) {
            this.levelRequired = levelRequired;
            this.baseEffect = baseEffect;
            this.damageType = damageType;
            this.dmgRate = dmgRate;
        }

        public boolean presentOn(LivingEntity entity) {
            return this == BURN ? entity.isOnFire() : entity.hasStatusEffect(baseEffect);
        }

        public int getDurationFor(LivingEntity entity) {
            if (!this.presentOn(entity)) {
                return 0;
            }

            if (this == BURN) {
                return entity.getFireTicks();
            }
            else {
                StatusEffectInstance statusEffect = entity.getStatusEffect(baseEffect);
                return statusEffect.isInfinite() ? 99999 : statusEffect.getDuration();
            }
        }

        public int getAmplifierFor(LivingEntity entity) {
            if (!this.presentOn(entity)) {
                return -1;
            }

            return this == BURN ? 0 : entity.getStatusEffect(baseEffect).getAmplifier();
        }

        public void inflictOn(LivingEntity entity) {
            int oldDuration = this.getDurationFor(entity);
            int newDuration = Math.min(360, 120 + oldDuration);

            if (this == BURN) {
                entity.setOnFireFor(newDuration / 20);
            }
            else {
                entity.addStatusEffect(new StatusEffectInstance(baseEffect, newDuration, 0));
            }

            SatisfyingWeapons.LOGGER.info("Inflicting " + this.name() + " for " + newDuration + " ticks");
        }

        public float calculateDamageFor(LivingEntity entity) {
            int duration = this.getDurationFor(entity);
            int amplifier = this.getAmplifierFor(entity);

            amplifier = Math.min(dmgRate.length - 1, amplifier);
            float totalDamage = duration / dmgRate[amplifier];

            if (this == POISON) {
                totalDamage = Math.max(0, (Math.min(totalDamage, entity.getHealth() - 1)));
            }

            return totalDamage;
        }

        public void detonateFor(LivingEntity entity, float multiplier) {
            float totalDamage = calculateDamageFor(entity) * multiplier;
            entity.damage(CombatHelper.getDamageSource(damageType, entity.getWorld()), totalDamage);

            if (this == BURN) {
                entity.setFireTicks(0);
            }
            else {
                entity.removeStatusEffect(baseEffect);
            }

            SatisfyingWeapons.LOGGER.info("Detonating " + this.name() + " for " + totalDamage + " damage");
        }

        public static ArrayList<DoT> getDotsForLevel(int weaponLevel) {
            return new ArrayList<>(Arrays.stream(DoT.values())
                    .filter(dot -> weaponLevel >= dot.levelRequired)
                    .toList());
        }

        public static List<DoT> getDotsOn(LivingEntity entity, int weaponLevel) {
            return new ArrayList<>(getDotsForLevel(weaponLevel).stream()
                    .filter(dot -> dot.presentOn(entity))
                    .toList());
        }
    }
}
