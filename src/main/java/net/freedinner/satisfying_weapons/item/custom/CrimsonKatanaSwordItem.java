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

        // Poison goes first because it's non-lethal
        StatusEffectInstance poison = target.getStatusEffect(StatusEffects.POISON);
        if (poison != null && this.getLevel() >= 2) {
            int[] dmgRate = {25, 12, 6, 3, 1};
            int amplifier = Math.min(4, poison.getAmplifier());
            float totalDamage = poison.getDuration() / dmgRate[amplifier];
            totalDamage = Math.max(0, (Math.min(totalDamage, target.getHealth() - 1)));

            attacker.sendMessage(Text.literal("Detonating poison for " + totalDamage + " damage"));
            target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_POISON, world), totalDamage);
            target.removeStatusEffect(StatusEffects.POISON);
        }

        StatusEffectInstance wither = target.getStatusEffect(StatusEffects.WITHER);
        if (wither != null) {
            int[] dmgRate = {40, 20, 10, 5, 2, 1};
            int amplifier = Math.min(5, wither.getAmplifier());
            int totalDamage = wither.getDuration() / dmgRate[amplifier];

            attacker.sendMessage(Text.literal("Detonating wither for " + totalDamage + " damage"));
            target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_WITHER, world), totalDamage);
            target.removeStatusEffect(StatusEffects.WITHER);
        }

        if (target.isOnFire() && this.getLevel() >= 4) {
            int dmgRate = 20;
            int totalDamage = target.getFireTicks() / dmgRate;

            attacker.sendMessage(Text.literal("Detonating burn for " + totalDamage + " damage"));
            target.damage(CombatHelper.getDamageSource(ModDamageTypes.INSTANT_BURN, world), totalDamage);
            target.setFireTicks(0);
        }

        return super.postHit(stack, target, attacker);
    }
}
