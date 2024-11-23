package net.freedinner.satisfying_weapons.entity.renderer;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.entity.custom.BirthdayGiftEntity;
import net.freedinner.satisfying_weapons.entity.model.BirthdayGiftEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class BirthdayGiftEntityRenderer extends EntityRenderer<BirthdayGiftEntity> {
    protected BirthdayGiftEntityModel model;

    public BirthdayGiftEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BirthdayGiftEntityModel(context.getPart(ModEntities.BIRTHDAY_GIFT_MODEL_LAYER));
    }

    @Override
    public void render(BirthdayGiftEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Setup proper scaling and position
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(0.0f, -1.5f, 0.0f);

        // Slow rotation around the Y axis
        float angle = (entity.age + tickDelta) / 20f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(angle));

        // Slow movement up and down
        double offset = Math.sin((entity.age + tickDelta) * 0.1f) / 6;
        matrices.translate(0.0f, offset, 0.0f);

        // If detonated, add visual effects
        int overlay = 10 << 16;
        int detonationProgress = entity.getDetonationProgress();
        if (detonationProgress != -1) {
            // Rapid blinking with white
            overlay = (detonationProgress / 3 % 2 == 0) ? 15 | 10 << 16 : overlay;

            // Expand in size
            float scaleFactor = 1.0f + detonationProgress * 0.04f;
            matrices.scale(scaleFactor, scaleFactor, scaleFactor);
            matrices.translate(0.0f, -0.04f * detonationProgress, 0.0f);
        }

        // Render the base texture, with white overlay if needed
        Identifier texture = this.getTexture(entity);
        RenderLayer renderLayer = this.model.getLayer(texture);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        this.model.render(matrices, vertexConsumer, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

        matrices.pop();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(BirthdayGiftEntity entity) {
        return SatisfyingWeapons.id( "textures/entity/birthday_gift.png");
    }

    @Override
    protected boolean hasLabel(BirthdayGiftEntity entity) {
        return false;
    }
}
