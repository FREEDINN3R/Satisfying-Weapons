package net.freedinner.satisfying_weapons.entity.model;

import net.freedinner.satisfying_weapons.entity.custom.BirthdayGiftEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.9.4

public class BirthdayGiftEntityModel extends EntityModel<BirthdayGiftEntity> {
    private final ModelPart root;

    public BirthdayGiftEntityModel(ModelPart root) {
        this.root = root.getChild("bb_main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        main.addChild("top1", ModelPartBuilder.create().uv(0, 8).cuboid(0.0F, 12.0F, -3.5F, 0.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        main.addChild("top2", ModelPartBuilder.create().uv(16, 8).cuboid(0.0F, 12.0F, -3.5F, 0.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(BirthdayGiftEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }
}
