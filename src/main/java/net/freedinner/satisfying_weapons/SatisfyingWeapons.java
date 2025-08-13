package net.freedinner.satisfying_weapons;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.freedinner.satisfying_weapons.block.ModBlocks;
import net.freedinner.satisfying_weapons.datagen.ModTags;
import net.freedinner.satisfying_weapons.effect.ModEffects;
import net.freedinner.satisfying_weapons.entity.ModEntities;
import net.freedinner.satisfying_weapons.event.ModEvents;
import net.freedinner.satisfying_weapons.event.custom.GlassSwordDeathHandler;
import net.freedinner.satisfying_weapons.gui.ModScreenHandlers;
import net.freedinner.satisfying_weapons.loot.ModLootConditions;
import net.freedinner.satisfying_weapons.loot.ModLootTablesModifier;
import net.freedinner.satisfying_weapons.item.ModItemGroups;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.networking.ModNetworking;
import net.freedinner.satisfying_weapons.particle.ModParticles;
import net.freedinner.satisfying_weapons.sound.ModSounds;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SatisfyingWeapons implements ModInitializer {
	public static final String MOD_ID = "satisfying_weapons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModItems.registerItems();
		ModBlocks.registerBlocks();
		ModItemGroups.registerItemGroups();

		ModEntities.registerEntities();
        ModEffects.registerEffects();

		ModTags.registerTags();

		ModParticles.registerParticles();
		ModSounds.registerSounds();

		ModLootConditions.registerLootConditions();
		ModLootTablesModifier.modifyLootTables();

		ModEvents.registerEvents();
		ModNetworking.registerC2SPackets();

		ModScreenHandlers.registerScreenHandlers();
	}
}