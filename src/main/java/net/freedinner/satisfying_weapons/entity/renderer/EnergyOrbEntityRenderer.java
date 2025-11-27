package net.freedinner.satisfying_weapons.entity.renderer;

import net.freedinner.satisfying_weapons.entity.custom.EnergyOrbEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.math.BlockPos;

public class EnergyOrbEntityRenderer<T extends Entity & FlyingItemEntity> extends FlyingItemEntityRenderer<EnergyOrbEntity> {
    public EnergyOrbEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(EnergyOrbEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLight(EnergyOrbEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected boolean hasLabel(EnergyOrbEntity entity) {
        return false;
    }
}
