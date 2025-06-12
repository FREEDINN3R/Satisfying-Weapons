package net.freedinner.satisfying_weapons.particle.custom;

import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class EntropyParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;
    protected EntropyParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider, double dx, double dy, double dz) {
        super(clientWorld, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 0.96f;
        this.gravityStrength = -0.15f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = dx;
        this.velocityY = dy;
        this.velocityZ = dz;
        this.scale = 0.3f;

        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);

        this.ascending = true;
        this.collidesWithWorld = false;
        this.maxAge = 8 * MathUtils.randomNumber(2, 3);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightness(float tint) {
        return 0x0000D0;
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteForAge(this.spriteProvider);

        if (this.isInvisible()) {
            this.setAlpha(0f);
        } else {
            this.setAlpha(1f);
        }
    }

    private boolean isInvisible() {
        // Copied from vanilla SpellParticle class
        // Hide particles if looking through a spyglass
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        return clientPlayerEntity != null
                && clientPlayerEntity.getEyePos().squaredDistanceTo(this.x, this.y, this.z) <= 9.0
                && minecraftClient.options.getPerspective().isFirstPerson()
                && clientPlayerEntity.isUsingSpyglass();
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider sprites) {
            this.spriteProvider = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new EntropyParticle(world, x, y, z, spriteProvider, dx, dy, dz);
        }
    }
}
