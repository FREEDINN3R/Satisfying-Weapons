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
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MechanicalSwordItem extends UpgradeableSwordItem {
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
        if(user.getStackInHand(Hand.OFF_HAND).getItem() instanceof ShieldItem) {
            user.setCurrentHand(Hand.OFF_HAND);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        user.setCurrentHand(hand);
        user.setSprinting(false);

        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int charge = ((IPlayerDataSaver) player).getGreatswordChargeLevel();

            if (charge < 5 * 60) {
                charge++;
                ((IPlayerDataSaver) player).setGreatswordChargeLevel(charge);

                if (charge % 60 == 0) {
                    float pitch = 0.6f + 0.1f * charge / 60;
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), SoundCategory.PLAYERS, 1.0f, pitch);
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!selected && entity instanceof PlayerEntity player) {
            ((IPlayerDataSaver) player).setGreatswordChargeLevel(0);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int chargeLevel = ((IPlayerDataSaver) player).getGreatswordChargeLevel() / 60;
            ((IPlayerDataSaver) player).setGreatswordChargeLevel(0);

            if (world.isClient() || chargeLevel < 1) {
                return;
            }

            // Energy discharge summoning
            EnergyDischargeEntity energyDischarge = new EnergyDischargeEntity(world, player);
            energyDischarge.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.0f, 0f);
            energyDischarge.setChargeLevel(chargeLevel);

            world.spawnEntity(energyDischarge);

            // Recoil
            Vec3d v = Vec3d.fromPolar(player.getPitch(), player.getYaw()).normalize().multiply(-0.25 * chargeLevel);
            player.addVelocity(v);
            player.velocityModified = true;

            // Other stuff
            /*world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,
                    0.6f, PitchHelper.get());*/
            stack.damage(2 * chargeLevel, player, p -> p.sendToolBreakStatus(user.getActiveHand()));
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }
}
