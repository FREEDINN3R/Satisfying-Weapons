package net.freedinner.satisfying_weapons.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GlassCutSlashParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    protected GlassCutSlashParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 1f;
        this.gravityStrength = 0f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.scale = 0.6f;

        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);

        this.maxAge = 6;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightness(float tint) {
        return Math.max(128, super.getBrightness(tint));
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteForAge(this.spriteProvider);
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider sprites) {
            this.spriteProvider = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new GlassCutSlashParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
