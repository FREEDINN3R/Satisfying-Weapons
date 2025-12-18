package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.datagen.ModDamageTypes;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.CombatHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrimsonKatanaSwordItem extends UpgradeableSwordItem {
    public CrimsonKatanaSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable CrimsonKatanaSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDead()) {
            return super.postHit(stack, target, attacker);
        }

        World world = target.getWorld();

        float poisonDamage = 0, witherDamage = 0, burnDamage = 0;
        int effectCount = 0;

        // Poison goes first because it's non-lethal
        StatusEffectInstance poison = target.getStatusEffect(StatusEffects.POISON);
        if (poison != null && this.getLevel() >= 2) {
            int[] dmgRate = {25, 12, 6, 3, 1};
            int amplifier = Math.min(4, poison.getAmplifier());
            poisonDamage = poison.getDuration() / dmgRate[amplifier];
            poisonDamage = Math.max(0, (Math.min(poisonDamage, target.getHealth() - 1)));

            target.removeStatusEffect(StatusEffects.POISON);
            effectCount++;
        }

        StatusEffectInstance wither = target.getStatusEffect(StatusEffects.WITHER);
        if (wither != null) {
            int[] dmgRate = {40, 20, 10, 5, 2, 1};
            int amplifier = Math.min(5, wither.getAmplifier());
            witherDamage = wither.getDuration() / dmgRate[amplifier];

            target.removeStatusEffect(StatusEffects.WITHER);
            effectCount++;
        }

        if (target.isOnFire() && this.getLevel() >= 4) {
            int dmgRate = 20;
            burnDamage = target.getFireTicks() / dmgRate;

            target.setFireTicks(0);
            effectCount++;
        }

        if (effectCount >= 2 && this.getLevel() >= 2) {
            poisonDamage *= 1.2f;
            witherDamage *= 1.2f;
            burnDamage *= 1.2f;
        }

        attacker.sendMessage(Text.literal("poison = " + poisonDamage + "; wither = " + witherDamage + "; burn = " + burnDamage));

        target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_POISON, world), poisonDamage);
        target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_WITHER, world), witherDamage);
        target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_BURN, world), burnDamage);

        return super.postHit(stack, target, attacker);
    }
}
