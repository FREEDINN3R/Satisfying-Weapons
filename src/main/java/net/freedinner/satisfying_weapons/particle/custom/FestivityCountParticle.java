package net.freedinner.satisfying_weapons.particle.custom;

import net.freedinner.satisfying_weapons.particle.ParticleSheetRenderOnTop;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class FestivityCountParticle extends SpriteBillboardParticle {
    protected FestivityCountParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteSet, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 1f;
        this.gravityStrength = 0;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = dx;
        this.velocityY = dy;
        this.velocityZ = dz;
        this.scale = 0.2f;
        this.maxAge = 20;
        this.setSpriteForAge(spriteSet);

        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleSheetRenderOnTop.INSTANCE;
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider sprites) {
            this.spriteProvider = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new FestivityCountParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
