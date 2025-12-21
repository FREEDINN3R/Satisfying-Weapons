package net.freedinner.satisfying_weapons.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.particle.custom.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ModParticles {
    // Firework Sword
    public static final List<DefaultParticleType> FESTIVITY_COUNT = registerMany("festivity_count", 10);

    // Glass Sword
    public static final DefaultParticleType BLOOD = register("blood");
    public static final List<DefaultParticleType> GLASS_CUT_SLASH = registerMany("glass_cut_slash", 2);

    // Crimson Katana
    public static final DefaultParticleType CRIMSON_SPARK = register("crimson_spark");
    public static final DefaultParticleType CRIMSON_SPARK_GLOW = register("crimson_spark_glow");

    // Toy Bow
    public static final DefaultParticleType CONFETTI = register("confetti");

    // Sword of Dying Star
    public static final DefaultParticleType WHITE_LINE = register("white_line");
    public static final DefaultParticleType DARK_LINE = register("dark_line");
    public static final DefaultParticleType ENTROPY = register("entropy");

    private static DefaultParticleType register(String name) {
        DefaultParticleType particleType = FabricParticleTypes.simple();
        Registry.register(Registries.PARTICLE_TYPE, SatisfyingWeapons.id(name), particleType);
        return particleType;
    }

    private static List<DefaultParticleType> registerMany(String name, int count) {
        List<DefaultParticleType> list = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            list.add(register(name + "_" + i));
        }

        return list;
    }

    public static void registerParticles() {
        SatisfyingWeapons.LOGGER.info("Registering server-side particles");
    }

    public static void registerParticlesClient() {
        SatisfyingWeapons.LOGGER.info("Registering client-side particles");

        // Firework Sword
        registerManyClient(FESTIVITY_COUNT, FestivityCountParticle.Factory::new);

        // Glass Sword
        ParticleFactoryRegistry.getInstance().register(BLOOD, BloodParticle.Factory::new);
        registerManyClient(GLASS_CUT_SLASH, GlassCutSlashParticle.Factory::new);

        // Crimson Katana
        ParticleFactoryRegistry.getInstance().register(CRIMSON_SPARK, CrimsonSparkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CRIMSON_SPARK_GLOW, CrimsonSparkGlowParticle.Factory::new);

        // Toy Bow
        ParticleFactoryRegistry.getInstance().register(CONFETTI, ConfettiParticle.Factory::new);

        // Sword of Dying Star
        ParticleFactoryRegistry.getInstance().register(WHITE_LINE, LineParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(DARK_LINE, LineParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ENTROPY, EntropyParticle.Factory::new);
    }

    private static void registerManyClient(List<DefaultParticleType> particleList, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> factory) {
        for (DefaultParticleType particle : particleList) {
            ParticleFactoryRegistry.getInstance().register(particle, factory);
        }
    }
}
