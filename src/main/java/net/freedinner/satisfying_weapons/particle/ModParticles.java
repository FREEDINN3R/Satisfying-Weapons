package net.freedinner.satisfying_weapons.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.particle.custom.ConfettiParticle;
import net.freedinner.satisfying_weapons.particle.custom.FestivityCountParticle;
import net.freedinner.satisfying_weapons.particle.custom.LineParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ModParticles {
    public static final List<DefaultParticleType> FESTIVITY_COUNT = registerMany("festivity_count", 10);
    public static final DefaultParticleType CONFETTI = register("confetti");
    public static final DefaultParticleType WHITE_LINE = register("white_line");
    public static final DefaultParticleType DARK_LINE = register("dark_line");

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

        for (DefaultParticleType particle : FESTIVITY_COUNT) {
            ParticleFactoryRegistry.getInstance().register(particle, FestivityCountParticle.Factory::new);
        }

        ParticleFactoryRegistry.getInstance().register(CONFETTI, ConfettiParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.WHITE_LINE, LineParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.DARK_LINE, LineParticle.Factory::new);
    }
}
