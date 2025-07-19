package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.entity.custom.BlackHoleEntity;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DyingStarSwordItem extends UpgradeableSwordItem {
    public DyingStarSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable DyingStarSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        user.getWorld().playSound(null, user.getBlockPos(), ModSounds.BLACK_HOLE_THROWN, SoundCategory.MASTER,
                0.8f, PitchUtils.get());

        if (!world.isClient) {
            boolean shouldCollectLoot = this.getLevel() >= 2 && user.isSneaking();

            // Spawning BH
            BlackHoleEntity blackHole = new BlackHoleEntity(world, user, this.getLevel(), shouldCollectLoot);
            blackHole.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, BlackHoleEntity.BLACK_HOLE_SPEED, 1.0f);
            world.spawnEntity(blackHole);

            int durabilityCost = (this.getLevel() < 2) ? 3 : 1;
            itemStack.damage(durabilityCost, user, player -> player.sendToolBreakStatus(hand));
        }

        ItemCooldownManager cooldownManager = user.getItemCooldownManager();
        int cooldown = (this.getLevel() < 4) ? 60 : 40;
        this.setCooldown(cooldownManager, cooldown);

        return TypedActionResult.success(itemStack, world.isClient);
    }

    private void setCooldown(ItemCooldownManager cooldownManager, int cooldown) {
        for (Item swordLevel : ModItems.SWORD_OF_DYING_STAR) {
            cooldownManager.set(swordLevel, cooldown);
        }
    }
}
