package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToyBowItem extends UpgradeableBowItem {
    public ToyBowItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableBowItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if (Screen.hasShiftDown()) switch (this.getLevel()) {
            case 1:
                tooltip.add(Text.literal("Shoots Toy Arrows, which apply Birthday Party").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("for 10s on hit. During Birthday Party, the mob will").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("continuously taunt surrounding mobs to attack them").formatted(Formatting.GRAY));
                break;
            case 2:
                tooltip.add(Text.literal("Greatly increases accuracy and range of this bow.").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Also, 50% chance to not waste an arrow while shooting.").formatted(Formatting.GRAY));
                break;
            case 3:
                tooltip.add(Text.literal("When applying Birthday Party with Toy Arrow, summon").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Birthday Gift above the entity's head. After Birthday").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Party ends, or entity dies, Birthday Gift falls down").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("and explodes, damaging entities around it").formatted(Formatting.GRAY));
                break;
            case 4:
                tooltip.add(Text.literal("Entities with Birthday Party are slowed down,").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("and receive 15% increased damage.").formatted(Formatting.GRAY));
                break;
            case 5:
                tooltip.add(Text.literal("Hitting a Birthday Gift with a Toy Arrow will detonate").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("it, causing it to explode and fire 5 identical Toy Arrows").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("in random directions. These arrows will prioritize hitting").formatted(Formatting.GRAY));
                tooltip.add(Text.literal("other Birthday Gifts, and mobs without Birthday Party.").formatted(Formatting.GRAY));
                break;
        }
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
        float pitch = PitchUtils.get() - 0.3f + pullProgress * 0.5f;
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, pitch);

        // Arrow used stat
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }
}
