package net.freedinner.satisfying_weapons.entity.renderer;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.ModEntitiesClient;
import net.freedinner.satisfying_weapons.entity.custom.NoxiousSlashEntity;
import net.freedinner.satisfying_weapons.entity.model.NoxiousSlashEntityModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NoxiousSlashEntityRenderer extends EntityRenderer<NoxiousSlashEntity> {
    protected NoxiousSlashEntityModel model;

    public NoxiousSlashEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new NoxiousSlashEntityModel(context.getPart(ModEntitiesClient.NOXIOUS_SLASH_MODEL_LAYER));
    }

    @Override
    public void render(NoxiousSlashEntity slash, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(slash, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(NoxiousSlashEntity entity) {
        return SatisfyingWeapons.id( "textures/entity/texture.png");
    }

    @Override
    protected boolean hasLabel(NoxiousSlashEntity entity) {
        return false;
    }
}
