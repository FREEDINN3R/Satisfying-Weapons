package net.freedinner.satisfying_weapons.particle.custom;

import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class BloodParticle extends SpriteBillboardParticle {
    protected BloodParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteSet, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 1f;
        this.gravityStrength = 1f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = dx;
        this.velocityY = dy;
        this.velocityZ = dz;
        this.scale = 0.04f;
        this.maxAge = MathUtils.randomNumber(40, 100);
        this.setSpriteForAge(spriteSet);

        this.red = 0.8f;
        this.green = 0.05f;
        this.blue = 0f;
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
            return new BloodParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
