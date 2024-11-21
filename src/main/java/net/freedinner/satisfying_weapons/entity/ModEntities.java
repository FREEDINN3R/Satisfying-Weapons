package net.freedinner.satisfying_weapons.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
import net.freedinner.satisfying_weapons.entity.renderer.ToyArrowEntityRenderer;
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

    public static void registerEntities() {
        SatisfyingWeapons.LOGGER.info("Registering entities");
    }

    public static void registerEntitiesClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side entities");

        EntityRendererRegistry.register(ModEntities.TOY_ARROW, ToyArrowEntityRenderer::new);
    }
}
