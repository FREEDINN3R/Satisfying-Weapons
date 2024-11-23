package net.freedinner.satisfying_weapons.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.custom.BirthdayGiftEntity;
import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
import net.freedinner.satisfying_weapons.entity.model.BirthdayGiftEntityModel;
import net.freedinner.satisfying_weapons.entity.renderer.BirthdayGiftEntityRenderer;
import net.freedinner.satisfying_weapons.entity.renderer.ToyArrowEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {
    public static final EntityType<ToyArrowEntity> TOY_ARROW = Registry.register(
            Registries.ENTITY_TYPE,
            SatisfyingWeapons.id("toy_arrow"),
            FabricEntityTypeBuilder.<ToyArrowEntity>create(SpawnGroup.MISC, ToyArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeChunks(4).trackedUpdateRate(20)
                    .build()
    );

    public static final EntityType<BirthdayGiftEntity> BIRTHDAY_GIFT = Registry.register(
            Registries.ENTITY_TYPE,
            SatisfyingWeapons.id("birthday_gift"),
            FabricEntityTypeBuilder.<BirthdayGiftEntity>create(SpawnGroup.MISC, BirthdayGiftEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.65f))
                    .trackedUpdateRate(1)
                    .build()
    );

    public static void registerEntities() {
        SatisfyingWeapons.LOGGER.info("Registering entities");
    }

    @Environment(EnvType.CLIENT)
    public static final EntityModelLayer BIRTHDAY_GIFT_MODEL_LAYER = new EntityModelLayer(
            SatisfyingWeapons.id("birthday_gift"),
            "birthday_gift_model_layer"
    );

    public static void registerEntitiesClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side entities");

        EntityRendererRegistry.register(ModEntities.TOY_ARROW, ToyArrowEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BIRTHDAY_GIFT, BirthdayGiftEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(BIRTHDAY_GIFT_MODEL_LAYER, BirthdayGiftEntityModel::getTexturedModelData);
    }
}
