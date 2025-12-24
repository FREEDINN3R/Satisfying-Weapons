package net.freedinner.satisfying_weapons.mixin;

import com.google.common.collect.ImmutableList;
import net.freedinner.satisfying_weapons.particle.ParticleSheetRenderOnTop;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {
    @Final
    @Shadow
    private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    static {
        List<ParticleTextureSheet> original = PARTICLE_TEXTURE_SHEETS;
        PARTICLE_TEXTURE_SHEETS = ImmutableList.<ParticleTextureSheet>builder()
                .addAll(original)
                .add(ParticleSheetRenderOnTop.INSTANCE)
                .build();
    }
}