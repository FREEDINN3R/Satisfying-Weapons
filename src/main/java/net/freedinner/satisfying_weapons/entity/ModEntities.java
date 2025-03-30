package net.freedinner.satisfying_weapons.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.custom.ActiveBlackHoleEntity;
import net.freedinner.satisfying_weapons.entity.custom.BirthdayGiftEntity;
import net.freedinner.satisfying_weapons.entity.custom.ThrownBlackHoleEntity;
import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
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
                    .trackRangeChunks(4).trackedUpdateRate(10)
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

    public static final EntityType<ThrownBlackHoleEntity> THROWN_BLACK_HOLE = Registry.register(
            Registries.ENTITY_TYPE,
            SatisfyingWeapons.id("thrown_black_hole"),
            FabricEntityTypeBuilder.<ThrownBlackHoleEntity>create(SpawnGroup.MISC, ThrownBlackHoleEntity::new)
                    .dimensions(EntityDimensions.fixed(0.15f, 0.15f))
                    .trackRangeChunks(4).trackedUpdateRate(10)
                    .build()
    );

    public static final EntityType<ActiveBlackHoleEntity> ACTIVE_BLACK_HOLE = Registry.register(
            Registries.ENTITY_TYPE,
            SatisfyingWeapons.id("active_pocket_vortex"),
            FabricEntityTypeBuilder.<ActiveBlackHoleEntity>create(SpawnGroup.MISC, ActiveBlackHoleEntity::new)
                    .dimensions(EntityDimensions.fixed(0.15f, 0.15f))
                    .trackRangeChunks(4).trackedUpdateRate(10)
                    .build()
    );

    public static void registerEntities() {
        SatisfyingWeapons.LOGGER.info("Registering entities");
    }
}
