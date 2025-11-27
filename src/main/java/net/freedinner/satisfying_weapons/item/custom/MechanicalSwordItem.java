package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.custom.EnergyOrbEntity;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.mixin.LivingEntityAccessor;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.freedinner.satisfying_weapons.util.PitchUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
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
        ItemStack itemStack = user.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            return TypedActionResult.pass(itemStack);
        }

        user.setCurrentHand(hand);
        user.setSprinting(false);

        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }

        int usageTime = user.getItemUseTime();

        if ((usageTime + STARTING_CHARGE) % this.getChargeRate() == 0) {
            this.updateChargeLevel(player, false);
        }

        if (this.getLevel() >= 2) {
            user.addStatusEffect(new StatusEffectInstance(ModEffects.METAL_HEART, 1, 0, false, false, true));
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient() || !(user instanceof PlayerEntity player)) {
            return;
        }

        int usageTime = user.getItemUseTime();
        int chargeLevel = this.getChargeLevel(usageTime);

        if (chargeLevel < 1) {
            return;
        }

        this.shootEnergyDischarge(chargeLevel, user);

        if (this.getLevel() >= 2) {
            int duration = 30 + 10 * chargeLevel;
            user.addStatusEffect(new StatusEffectInstance(ModEffects.METAL_HEART, duration, 0, false, false, true));
        }

        // Durability cost
        stack.damage(chargeLevel, player, p -> p.sendToolBreakStatus(user.getActiveHand()));

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.6f, PitchUtils.get());
    }

    public void updateChargeLevel(PlayerEntity player, boolean bonusCharge) {
        if (player.getWorld().isClient) {
            return;
        }

        if (player.getItemUseTime() > this.getMaxChargeTime() && this.getLevel() < 5) {
            return;
        }

        if (bonusCharge) {
            // Bonus charge means we need to manually increase itemUseTimeLeft
            int itemUseTimeLeft = player.getItemUseTimeLeft();
            itemUseTimeLeft -= this.getChargeRate();
            ((LivingEntityAccessor) player).setItemUseTimeLeft(itemUseTimeLeft);

            // Syncing itemUseTimeLeft with client
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(itemUseTimeLeft);
            ServerPlayNetworking.send((ServerPlayerEntity) player, ModNetworking.SYNC_USE_TIME_LEFT_ID, buf);
        }

        this.playChargeSound(player);

        if (player.getItemUseTime() > this.getMaxChargeTime() && this.getLevel() == 5) {
            this.shootEnergyDischarge(3, player);
        }
    }

    public void playChargeSound(LivingEntity user) {
        if (!user.isUsingItem() || !user.getStackInHand(Hand.MAIN_HAND).isOf(this)) {
            return;
        }

        float pitch = 0.6f + 0.1f * this.getChargeLevel(user.getItemUseTime());
        user.getWorld().playSound(null, user.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), SoundCategory.PLAYERS, 1.0f, pitch);
    }

    public void shootEnergyDischarge(int chargeLevel, LivingEntity user) {
        // Creating projectile
        EnergyOrbEntity energyDischarge = new EnergyOrbEntity(user.getWorld(), user, chargeLevel, this.getLevel());
        energyDischarge.setVelocity(user, user.getPitch(), user.getYaw(), user.getRoll(), 1.0f, 0.5f);

        user.getWorld().spawnEntity(energyDischarge);

        // Recoil
        Vec3d dir = Vec3d.fromPolar(user.getPitch(), user.getYaw()).normalize();
        Vec3d v = dir.multiply(-0.2 * (chargeLevel - 1));
        user.addVelocity(v);
        user.velocityModified = true;
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

    public int getMaxChargeLevel() {
        return (this.getLevel() < 4) ? 4 : 5;
    }

    public int getMaxChargeTime() {
        return this.getMaxChargeLevel() * this.getChargeRate() - STARTING_CHARGE;
    }

    public int getChargeLevel(int usageTime) {
        return Math.min((usageTime + STARTING_CHARGE) / this.getChargeRate(), this.getMaxChargeLevel());
    }
}
