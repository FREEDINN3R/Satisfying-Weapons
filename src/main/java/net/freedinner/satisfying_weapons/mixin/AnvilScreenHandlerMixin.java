package net.freedinner.satisfying_weapons.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.ModToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {
    @WrapOperation(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 0))
    private int modifyRepairCost1(ItemStack itemStack, Operation<Integer> original) {
        float costModifier = getCostModifier(itemStack);
        return Math.round(original.call(itemStack) * costModifier);
    }

    @WrapOperation(method = "updateResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 1))
    private int modifyRepairCost2(ItemStack itemStack, Operation<Integer> original) {
        float costModifier = getCostModifier(itemStack);
        return Math.round(original.call(itemStack) * costModifier);
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

            // Rare weapons require 20 shards
            if (material.equals(ModToolMaterial.LEGENDARY)) {
                return 0.2f;
            }
        }

        return 1.0f;
    }
}
