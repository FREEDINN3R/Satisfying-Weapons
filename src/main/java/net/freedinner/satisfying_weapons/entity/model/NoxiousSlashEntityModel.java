package net.freedinner.satisfying_weapons.entity.model;

import net.freedinner.satisfying_weapons.entity.custom.NoxiousSlashEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 4.9.4

public class NoxiousSlashEntityModel extends EntityModel<NoxiousSlashEntity> {
	private final ModelPart root;

	public NoxiousSlashEntityModel(ModelPart root) {
		this.root = root.getChild("bb_main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-16.0F, 0.0F, -8.0F, 32.0F, 0.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void setAngles(NoxiousSlashEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}