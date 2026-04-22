package net.freedinner.satisfying_weapons.entity.custom;

import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class NoxiousSlashEntity extends Entity {
    public NoxiousSlashEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    public NoxiousSlashEntity(World world, @NotNull LivingEntity target, LivingEntity owner) {
        super(ModEntities.NOXIOUS_SLASH, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
