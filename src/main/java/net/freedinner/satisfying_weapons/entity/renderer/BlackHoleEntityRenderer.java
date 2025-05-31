package net.freedinner.satisfying_weapons.entity.renderer;

import net.freedinner.satisfying_weapons.entity.custom.BlackHoleEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.math.BlockPos;

public class BlackHoleEntityRenderer<T extends Entity & FlyingItemEntity> extends FlyingItemEntityRenderer<BlackHoleEntity> {
    public BlackHoleEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(BlackHoleEntity blackHole, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // If active, animate the size changes
        if (blackHole.isActive()) {
            int activeAge = blackHole.getActiveAge();
            float scale = getScaleForAge(activeAge);
            matrices.scale(scale, scale, scale);
        }

        super.render(blackHole, yaw, tickDelta, matrices, vertexConsumers, light);

        matrices.pop();
    }

    private static float getScaleForAge(int activeAge) {
        float scale;

        if (activeAge <= BlackHoleEntity.BLACK_HOLE_GROWING_DURATION) {
            // Goes from 1.0 to 1.5
            scale = 1.0f + 0.5f * activeAge / BlackHoleEntity.BLACK_HOLE_GROWING_DURATION;
        }
        else if (activeAge <= BlackHoleEntity.BLACK_HOLE_MAX_ACTIVE_AGE - BlackHoleEntity.BLACK_HOLE_SHRINKING_DURATION) {
            // Alternates between 1.4 and 1.5
            scale = 1.0f + ((activeAge % 2 == 1) ? 0.4f : 0.5f);
        }
        else {
            // Goes from 1.5 to 0
            scale = 1.5f * (BlackHoleEntity.BLACK_HOLE_MAX_ACTIVE_AGE - activeAge) / BlackHoleEntity.BLACK_HOLE_SHRINKING_DURATION;
        }
        return scale;
    }


    @Override
    protected int getBlockLight(BlackHoleEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    protected boolean hasLabel(BlackHoleEntity entity) {
        return false;
    }
}