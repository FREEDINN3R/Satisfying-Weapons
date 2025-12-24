package net.freedinner.satisfying_weapons.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;

public class ParticleSheetRenderOnTop implements ParticleTextureSheet {
    public static ParticleSheetRenderOnTop INSTANCE = new ParticleSheetRenderOnTop();

    ParticleSheetRenderOnTop() {}

    @Override
    public void begin(BufferBuilder builder, TextureManager textureManager) {
        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
    }

    @Override
    public void draw(Tessellator tessellator) {
        tessellator.draw();
    }

    public String toString() {
        return "PARTICLE_SHEET_RENDER_ON_TOP";
    }
}
