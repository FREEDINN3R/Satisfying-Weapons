package net.freedinner.satisfying_weapons.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.freedinner.satisfying_weapons.effect.custom.FestivityEffect;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CrimsonKatanaSwordItem extends UpgradeableSwordItem {
    public CrimsonKatanaSwordItem(ModToolMaterial toolMaterial, Settings settings, int level, @Nullable CrimsonKatanaSwordItem nextLevelWeapon) {
        super(toolMaterial, settings, level, nextLevelWeapon);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        StatusEffectInstance poison = attacker.getStatusEffect(StatusEffects.POISON);
        if (poison != null) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, poison.getDuration(), poison.getAmplifier(), false, true));
            attacker.removeStatusEffect(StatusEffects.POISON);
        }

        StatusEffectInstance wither = attacker.getStatusEffect(StatusEffects.WITHER);
        if (wither != null) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, wither.getDuration(), wither.getAmplifier(), false, true));
            attacker.removeStatusEffect(StatusEffects.WITHER);
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (world.isClient()) {
            return TypedActionResult.consume(itemStack);
        }

        user.damage(world.getDamageSources().generic(), 0.001f);

        List<StatusEffect> dotList = Arrays.asList(StatusEffects.POISON, StatusEffects.WITHER);
        List<StatusEffect> copyList = List.copyOf(dotList);

        while (!dotList.isEmpty()) {
            StatusEffect effect = MathUtils.getRandomElement(dotList);
            StatusEffectInstance effectInstance = user.getStatusEffect(effect);
            int newAmplifier = 0;

            if (effectInstance != null) {
                newAmplifier = effectInstance.getAmplifier() + 1;

                if (newAmplifier > 2) {
                    dotList.remove(effect);
                    continue;
                }

                user.removeStatusEffect(effect);
            }

            user.addStatusEffect(new StatusEffectInstance(effect, 160, newAmplifier, false, true));
            break;
        }

        if (dotList.isEmpty()) {
            StatusEffect effect = MathUtils.getRandomElement(copyList);
            StatusEffectInstance effectInstance = user.getStatusEffect(effect);
            user.removeStatusEffect(effect);
            user.addStatusEffect(new StatusEffectInstance(effect, 160, effectInstance.getAmplifier(), false, true));
        }

        return super.use(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 8;
    }
}
