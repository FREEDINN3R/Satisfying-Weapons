package net.freedinner.satisfying_weapons.particle.custom;

import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class ConfettiParticle extends SpriteBillboardParticle {
    protected ConfettiParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteSet, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 1f;
        this.gravityStrength = 0.7f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = dx;
        this.velocityY = dy;
        this.velocityZ = dz;
        this.scale = 0.2f;
        this.maxAge = MathUtils.randomNumber(40, 60);
        this.setSpriteForAge(spriteSet);

        int i = 1 + MathUtils.randomNumber(6);
        this.red = (i / 4 == 1) ? 0.9f : 0.3f;
        this.green = (i % 4 / 2 == 1) ? 0.9f : 0.3f;
        this.blue = (i % 4 % 2 == 1) ? 0.9f : 0.3f;
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
            return new ConfettiParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
