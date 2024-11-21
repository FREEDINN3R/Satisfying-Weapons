package net.freedinner.satisfying_weapons.entity.renderer;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.custom.ToyArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class ToyArrowEntityRenderer extends ProjectileEntityRenderer<ToyArrowEntity> {
    public static final Identifier TEXTURE = SatisfyingWeapons.id("textures/entity/toy_arrow.png");

    public ToyArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ToyArrowEntity entity) {
        return TEXTURE;
    }
}