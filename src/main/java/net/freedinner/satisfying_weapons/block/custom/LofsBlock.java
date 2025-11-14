package net.freedinner.satisfying_weapons.block.custom;

import net.freedinner.satisfying_weapons.entity.misc.NonDestructiveExplosionBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LofsBlock extends Block {
    public LofsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
        tooltip.add(Text.literal("Let's Object For Something").formatted(Formatting.GRAY));
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.createExplosion(null, serverWorld.getDamageSources().explosion(null, null), new NonDestructiveExplosionBehavior(), pos.toCenterPos(), 4, false, World.ExplosionSourceType.BLOCK);
        }
    }
}
