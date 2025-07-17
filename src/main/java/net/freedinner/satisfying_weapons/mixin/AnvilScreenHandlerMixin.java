package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.freedinner.satisfying_weapons.item.custom.GlassSwordItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @Shadow private int repairItemUsage;

    @WrapOperation(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 0))
    private int modifyShardRepairCost1(ItemStack itemStack, Operation<Integer> original) {
        float costModifier = getCostModifier(itemStack);
        return Math.round(original.call(itemStack) * costModifier);
    }

    @WrapOperation(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 1))
    private int modifyShardRepairCost2(ItemStack itemStack, Operation<Integer> original) {
        float costModifier = getCostModifier(itemStack);
        return Math.round(original.call(itemStack) * costModifier);
    }

    @ModifyExpressionValue(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2))
    private boolean allowBrokenGlassRepair(
            boolean original, @Local(ordinal = 0) ItemStack slot1, @Local(ordinal = 2) ItemStack slot2,
            @Local(ordinal = 1) LocalRef<ItemStack> result, @Local(ordinal = 0) LocalIntRef expCost
            ) {
        if (GlassSwordItem.isBrokenGlass(slot1) && GlassSwordItem.isGlassPane(slot2)) {
            ItemStack repairedSword = slot1.copy();
            GlassSwordItem.setGlassState(repairedSword, GlassSwordItem.GlassState.INTACT);
            result.set(repairedSword);

            this.repairItemUsage = 1;
            expCost.set(2);

            return true;
        }

        return original;
    }

    @ModifyExpressionValue(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getRepairCost()I", ordinal = 0))
    private int reduceGlassRepairCost(int original, @Local(ordinal = 0) ItemStack slot1, @Local(ordinal = 2) ItemStack slot2) {
        if (GlassSwordItem.isBrokenGlass(slot1) && GlassSwordItem.isGlassPane(slot2)) {
            return (int) Math.sqrt(original);
        }

        return original;
    }

    @ModifyExpressionValue(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getNextCost(I)I"))
    private int preventGlassCostIncrease(int original, @Local(ordinal = 0) ItemStack slot1, @Local(ordinal = 2) ItemStack slot2) {
        if (!GlassSwordItem.isBrokenGlass(slot1) || !GlassSwordItem.isGlassPane(slot2)) {
            return original;
        }

        int t = original;
        while (AnvilScreenHandler.getNextCost(t) != original && t > -1) {
            t--;
        }

        if (t == -1) {
            t = (original - 1) / 2;
        }

        return t;
    }

    @Unique
    private static float getCostModifier(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ToolItem tool) {
            ToolMaterial material = tool.getMaterial();

            // Rare weapons require 10 shards
            if (material.equals(ModToolMaterial.RARE)) {
                return 0.41f;
            }

            // Epic weapons require 15 shards
            if (material.equals(ModToolMaterial.EPIC)) {
                return 0.27f;
            }

            // Legendary weapons require 20 shards
            if (material.equals(ModToolMaterial.LEGENDARY)) {
                return 0.2f;
            }
        }

        return 1.0f;
    }
}
