package net.freedinner.satisfying_weapons.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> GLASS_CUT = register("glass_cut");
    public static final RegistryKey<DamageType> INSTANT_WITHER = register("instant_wither");
    public static final RegistryKey<DamageType> INSTANT_POISON = register("instant_poison");
    public static final RegistryKey<DamageType> INSTANT_BURN = register("instant_burn");

    private static RegistryKey<DamageType> register(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, SatisfyingWeapons.id(name));
    }

    public static void generateDamageTypes(FabricDataGenerator.Pack pack) {
        SatisfyingWeapons.LOGGER.info("Generating damage types");

        pack.addProvider(ModDamageTypeProvider::new);
    }

    static class ModDamageTypeProvider extends FabricDynamicRegistryProvider {

        public ModDamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            addEntry(entries, GLASS_CUT, 0f, DamageEffects.HURT);
            addEntry(entries, INSTANT_WITHER, 0f, DamageEffects.HURT);
            addEntry(entries, INSTANT_POISON, 0f, DamageEffects.HURT);
            addEntry(entries, INSTANT_BURN, 0f, DamageEffects.BURNING);
        }

        private static void addEntry(Entries entries, RegistryKey<DamageType> damageType, float exhaustion, DamageEffects damageEffects) {
            entries.add(damageType, new DamageType(
                    SatisfyingWeapons.MOD_ID + "." + damageType.getValue().getPath(),
                    DamageScaling.NEVER,
                    exhaustion,
                    damageEffects
            ));
        }

        @Override
        public String getName() {
            return "Satisfying Weapons Damage Types";
        }
    }
}
