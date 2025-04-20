package net.freedinner.satisfying_weapons.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.model.BirthdayGiftEntityModel;
import net.freedinner.satisfying_weapons.entity.renderer.BirthdayGiftEntityRenderer;
import net.freedinner.satisfying_weapons.entity.renderer.BlackHoleEntityRenderer;
import net.freedinner.satisfying_weapons.entity.renderer.ToyArrowEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@Environment(EnvType.CLIENT)
public class ModEntitiesClient {
    public static final EntityModelLayer BIRTHDAY_GIFT_MODEL_LAYER = new EntityModelLayer(
            SatisfyingWeapons.id("birthday_gift"),
            "birthday_gift_model_layer"
    );

    public static void registerEntitiesClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side entities");

        EntityRendererRegistry.register(ModEntities.TOY_ARROW, ToyArrowEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BIRTHDAY_GIFT, BirthdayGiftEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.THROWN_BLACK_HOLE, BlackHoleEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ACTIVE_BLACK_HOLE, BlackHoleEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLACK_HOLE, BlackHoleEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BIRTHDAY_GIFT_MODEL_LAYER, BirthdayGiftEntityModel::getTexturedModelData);
    }
}
