package net.freedinner.satisfying_weapons.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class CrimsonSparkParticle extends SpriteBillboardParticle {
    public static final int MAX_AGE = 6;

    protected CrimsonSparkParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteSet, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 1f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = dx;
        this.velocityY = dy;
        this.velocityZ = dz;
        this.scale = 0.006f;
        this.maxAge = MAX_AGE;
        this.setSpriteForAge(spriteSet);

        this.red = 1f;
        this.green = 0.9f;
        this.blue = 0.93f;
    }

    @Override
    protected int getBrightness(float tint) {
        return 255;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider sprites) {
            this.spriteProvider = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new CrimsonSparkParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
