package net.freedinner.satisfying_weapons.item.custom;

import net.freedinner.satisfying_weapons.entity.custom.EnergyDischargeEntity;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.freedinner.satisfying_weapons.util.data.IPlayerDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MechanicalSwordItem extends UpgradeableSwordItem {
    public static final int STANDARD_CHARGE_RATE = 50; // For Level 1-3
    public static final int REDUCED_CHARGE_RATE = 40; // For Level 4-5
    public static final int STARTING_CHARGE = 20;

    public MechanicalSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable MechanicalSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.getWorld().playSound(null, target.getBlockPos(), ModSounds.METALLIC_THUD, SoundCategory.PLAYERS, 0.8f, PitchUtils.get());
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        user.setSprinting(false);

        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        int currentCharge = ((IPlayerDataSaver) player).sw$getChargeMS();

        if (currentCharge < this.getMaxCharge()) {
            currentCharge++;
            ((IPlayerDataSaver) player).sw$setChargeMS(currentCharge);

            if (currentCharge % this.getChargeRate() == 0) {
                float pitch = 0.6f + 0.1f * currentCharge / this.getChargeRate();
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), SoundCategory.PLAYERS, 1.0f, pitch);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MechanicalSwordItem) {
            return;
        }

        ((IPlayerDataSaver) player).sw$setChargeMS(STARTING_CHARGE);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        int chargeLevel = ((IPlayerDataSaver) player).sw$getChargeMS() / this.getChargeRate();
        ((IPlayerDataSaver) player).sw$setChargeMS(STARTING_CHARGE);

        if (world.isClient() || chargeLevel < 1) {
            return;
        }

        // Projectile spawning
        EnergyDischargeEntity energyDischarge = new EnergyDischargeEntity(world, player);
        energyDischarge.setVelocity(user, user.getPitch(), user.getYaw(), user.getRoll(), 1.0f, 0.5f);
        energyDischarge.setChargeLevel(chargeLevel);

        world.spawnEntity(energyDischarge);

        // Recoil
        Vec3d dir = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize();
        Vec3d v = dir.multiply(-0.2 * (chargeLevel - 1));
        player.addVelocity(v);
        player.velocityModified = true;

        // Durability cost
        stack.damage(chargeLevel, player, p -> p.sendToolBreakStatus(user.getActiveHand()));

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.6f, PitchUtils.get());
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public int getChargeRate() {
        return (this.getLevel() < 4) ? STANDARD_CHARGE_RATE : REDUCED_CHARGE_RATE;
    }

    public int getMaxCharge() {
        return (this.getLevel() < 4) ? (4 * STANDARD_CHARGE_RATE) : (5 * REDUCED_CHARGE_RATE);
    }
}
