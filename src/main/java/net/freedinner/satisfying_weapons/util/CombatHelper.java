package net.freedinner.satisfying_weapons.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CombatHelper {
    public static boolean simulatePlayerAttack(PlayerEntity player, LivingEntity target, float damageMultiplier) {
        // If target can't be attacked, do nothing
        if (!target.isAttackable() || target.handleAttack(player)) {
            return false;
        }

        // Calculate total damage
        float playerGenericDamage = (float)player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float enchantmentDamage = EnchantmentHelper.getAttackDamage(player.getMainHandStack(), target.getGroup());
        float totalDamage = (playerGenericDamage + enchantmentDamage) * damageMultiplier;

        if (totalDamage <= 0) {
            return false;
        }

        // Save original health for stat increase
        float targetOriginalHealth = target.getHealth();

        // The actual damage part
        boolean damageSuccessful = target.damage(player.getDamageSources().playerAttack(player), totalDamage);

        if (!damageSuccessful) {
            return false;
        }

        // Blue particles from combat enchantments
        if (enchantmentDamage > 0) {
            player.addEnchantedHitParticles(target);
        }

        // Inform everyone else that damage happened
        player.onAttacking(target);
        EnchantmentHelper.onUserDamaged(target, player);
        EnchantmentHelper.onTargetDamaged(player, target);

        // Increase damage stat
        float actualDamage = targetOriginalHealth - target.getHealth();
        player.increaseStat(Stats.DAMAGE_DEALT, Math.round(actualDamage * 10.0f)); // Minecraft code wants it multiplied

        // If fire aspect, set on fire
        int fireAspectLevel = EnchantmentHelper.getFireAspect(player);
        if (fireAspectLevel > 0) {
            target.setOnFireFor(fireAspectLevel * 4);
        }

        // Dark red heart particles
        if (player.getWorld() instanceof ServerWorld serverWorld && actualDamage > 2.0f) {
            int particleCount = (int)(actualDamage * 0.5);
            serverWorld.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), particleCount, 0.1, 0.0, 0.1, 0.2);
        }

        return true;
    }

    public static DamageSource getDamageSource(RegistryKey<DamageType> modDamageType, World world, @Nullable Entity attacker) {
        return new DamageSource(
                world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(modDamageType),
                attacker
        );
    }
}
