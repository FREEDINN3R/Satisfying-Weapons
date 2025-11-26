package net.freedinner.satisfying_weapons.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("playerHitTimer")
    void setPlayerHitTimer(int playerHitTimer);

    @Accessor("itemUseTimeLeft")
    void setItemUseTimeLeft(int itemUseTimeLeft);

    @Accessor("lastDamageTaken")
    void setLastDamageTaken(float lastDamageTaken);
}
