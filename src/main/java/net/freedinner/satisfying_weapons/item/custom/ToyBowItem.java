package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ToyBowItem extends UpgradeableBowItem {
    public ToyBowItem(ToolMaterial toolMaterial, Settings settings, int level, @Nullable UpgradeableBowItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        ItemStack projectileStack = player.getProjectileType(stack);
        if (projectileStack.isEmpty() && !player.getAbilities().creativeMode) {
            return;
        }

        if (projectileStack.isEmpty()) {
            projectileStack = new ItemStack(Items.ARROW);
        }

        int useTime = this.getMaxUseTime(stack) - remainingUseTicks;
        float pullProgress = getPullProgress(useTime);

        if (pullProgress < 0.1) {
            return;
        }

        if (!world.isClient) {
            PersistentProjectileEntity toyArrow = new ToyArrowEntity(player, world);
            toyArrow.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, pullProgress * 2.0F, 1.0F);
            if (pullProgress == 1.0f) {
                toyArrow.setCritical(true);
            }

            int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
            if (powerLevel > 0) {
                toyArrow.setDamage(toyArrow.getDamage() + powerLevel * 0.5 + 0.5);
            }

            int punchLevel = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
            if (punchLevel > 0) {
                toyArrow.setPunch(punchLevel);
            }

            if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                toyArrow.setOnFireFor(100);
            }

            toyArrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

            world.spawnEntity(toyArrow);
            stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        }

        float pitch = PitchUtils.get() - 0.3f + pullProgress * 0.5f;
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, pitch);

        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) < 1 && !player.getAbilities().creativeMode) {
            projectileStack.decrement(1);

            if (projectileStack.isEmpty()) {
                player.getInventory().removeOne(projectileStack);
            }
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }
}
