package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.SoundUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ToyBowItem extends UpgradeableBowItem {
    public ToyBowItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableBowItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Only players can use this bow
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        // If no arrows and no creative
        ItemStack projectileStack = player.getProjectileType(stack);
        if (projectileStack.isEmpty() && !player.getAbilities().creativeMode) {
            return;
        }

        int useTime = this.getMaxUseTime(stack) - remainingUseTicks;
        float pullProgress = getPullProgress(useTime);

        if (pullProgress < 0.1f) {
            return;
        }

        if (!world.isClient) {
            // If level 2 or higher, increase speed and accuracy
            float speed, divergence;
            if (this.getLevel() >= 2) {
                speed = 3.5f;
                divergence = 0.0f;
            }
            else {
                speed = 2.5f;
                divergence = 1.0f;
            }

            // Create Toy Arrow and set velocity
            ToyArrowEntity toyArrow = new ToyArrowEntity(player, world);
            toyArrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, speed * pullProgress, divergence);

            if (pullProgress == 1.0f) {
                toyArrow.setCritical(true);
            }

            // Power enchantment
            int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
            if (powerLevel > 0) {
                toyArrow.setDamage(toyArrow.getDamage() + powerLevel * 0.5 + 0.5);
            }

            // Punch enchantment
            int punchLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
            if (punchLevel > 0) {
                toyArrow.setPunch(punchLevel);
            }

            // Flame enchantment
            if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                toyArrow.setOnFireFor(100);
            }

            toyArrow.setToyBowLevel(this.getLevel());
            toyArrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

            world.spawnEntity(toyArrow);
            stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));

            // If level 2 or higher, 50% chance to not waste an arrow
            boolean keepArrow = this.getLevel() >= 2 && MathUtils.takeChance(0.5, player.getWorld());

            // Remove 1 arrow if necessary
            if (!keepArrow && EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) < 1 && !player.getAbilities().creativeMode) {
                projectileStack.decrement(1);

                if (projectileStack.isEmpty()) {
                    player.getInventory().removeOne(projectileStack);
                }

                // Server client sync
                player.getInventory().markDirty();
            }
        }

        // Arrow shoot sound
        float pitch = SoundUtils.getPitch() - 0.3f + pullProgress * 0.5f;
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, pitch);

        // Arrow used stat
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }
}
