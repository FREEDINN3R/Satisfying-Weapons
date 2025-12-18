package net.freedinner.satisfying_weapons.item.custom;

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
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CrimsonKatanaSwordItem extends UpgradeableSwordItem {
    public CrimsonKatanaSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable CrimsonKatanaSwordItem nextLevelWeapon) {
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
            attacker.sendMessage(Text.literal("detonating " + dotEffect.name() + " for " + (dotEffect.calculateDamageFor(target) * dotMultiplier) + " damage"));
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

        public float calculateDamageFor(LivingEntity entity) {
            if (!this.presentOn(entity)) {
                return 0f;
            }

            int duration, amplifier;
            if (this == BURN) {
                duration = entity.getFireTicks();
                amplifier = 0;
            }
            else {
                StatusEffectInstance statusEffect = entity.getStatusEffect(baseEffect);
                duration = statusEffect.isInfinite() ? 99999 : statusEffect.getDuration();
                amplifier = statusEffect.getAmplifier();
            }

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
        }

        public static List<DoT> getDotsForLevel(int weaponLevel) {
            return Arrays.stream(DoT.values()).filter(dot -> weaponLevel >= dot.levelRequired).toList();
        }

        public static List<DoT> getDotsOn(LivingEntity entity, int weaponLevel) {
            return getDotsForLevel(weaponLevel).stream().filter(dot -> dot.presentOn(entity)).toList();
        }
    }
}
