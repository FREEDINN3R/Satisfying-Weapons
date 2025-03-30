package net.freedinner.satisfying_weapons.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.math.BlockPos;

public class BlackHoleEntityRenderer<T extends Entity & FlyingItemEntity> extends FlyingItemEntityRenderer<T> {
    public BlackHoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(T entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected boolean hasLabel(T entity) {
        return false;
    }
}