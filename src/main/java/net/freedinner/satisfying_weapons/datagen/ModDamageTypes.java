package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DeathMessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> GLASS_CUT = register("glass_cut");

    private static RegistryKey<DamageType> register(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, SatisfyingWeapons.id(name));
    }

    public static void generateDamageTypes(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating damage types");

        pack.addProvider(DamageTypeProvider::new);
    }

    static class DamageTypeProvider extends FabricDynamicRegistryProvider {

        public DamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            entries.add(GLASS_CUT, new DamageType(
                    SatisfyingWeapons.MOD_ID + ".glass_cut",
                    DamageScaling.NEVER,
                    0f,
                    DamageEffects.HURT
            ));
        }

        @Override
        public String getName() {
            return "Satisfying Weapons Damage Types";
        }
    }
}
