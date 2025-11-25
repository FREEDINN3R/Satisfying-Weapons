package net.freedinner.satisfying_weapons.entity.renderer;

import net.freedinner.satisfying_weapons.entity.custom.EnergyDischargeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.math.BlockPos;

public class EnergyDischargeEntityRenderer<T extends Entity & FlyingItemEntity> extends FlyingItemEntityRenderer<EnergyDischargeEntity> {
    public EnergyDischargeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLight(EnergyDischargeEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected int getSkyLight(EnergyDischargeEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected boolean hasLabel(EnergyDischargeEntity entity) {
        return false;
    }
}
