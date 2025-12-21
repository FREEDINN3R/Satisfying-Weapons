package net.freedinner.satisfying_weapons.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class CrimsonSparkGlowParticle extends CrimsonSparkParticle {
    protected CrimsonSparkGlowParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteSet, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, spriteSet, dx, dy, dz);

        this.scale = 0.02f;

        this.red = 1f;
        this.green = 0f;
        this.blue = 0.33f;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider sprites) {
            this.spriteProvider = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new CrimsonSparkGlowParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
