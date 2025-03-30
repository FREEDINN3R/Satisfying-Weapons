package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.entity.custom.ThrownBlackHoleEntity;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.IPlayerDataSaver;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
        if (world.isClient() || !hand.equals(Hand.MAIN_HAND)) {
            return super.use(world, user, hand);
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
        ItemCooldownManager cooldownManager = serverPlayer.getItemCooldownManager();

        ItemStack itemStack = user.getStackInHand(hand);

        if (cooldownManager.isCoolingDown(this)) {
            return TypedActionResult.pass(itemStack);
        }

        user.setCurrentHand(hand);
        user.swingHand(hand, true);
        user.getWorld().playSound(null, user.getBlockPos(), ModSounds.BLACK_HOLE_THROWN, SoundCategory.MASTER,
                0.6f, PitchUtils.get());
        itemStack.damage(2, user, player -> player.sendToolBreakStatus(hand));

        ThrownBlackHoleEntity pocketVortex = new ThrownBlackHoleEntity(world, user);
        pocketVortex.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, ThrownBlackHoleEntity.BLACK_HOLE_SPEED, 1.0f);
        world.spawnEntity(pocketVortex);

        cooldownManager.set(this, 40);

        return TypedActionResult.success(itemStack);
    }
}
