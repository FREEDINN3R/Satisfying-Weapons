package net.freedinner.satisfying_weapons.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.List;

public class ModItemGroups {
    public static ItemGroup SATISFYING_WEAPONS;

    static {
        SATISFYING_WEAPONS = Registry.register(
                Registries.ITEM_GROUP,
                SatisfyingWeapons.id("item_group"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("item.satisfying_weapons.item_group_name"))
                        .icon(() -> new ItemStack(ModItems.WISHING_STAR))
                        .entries(((displayContext, entries) -> {
                            entries.add(ModItems.UNFULFILLED_WISH);
                            entries.add(ModItems.WISHING_STAR);

                            entries.add(ModBlocks.UPGRADER);
                            entries.add(ModBlocks.DECONSTRUCTOR);

                            addAll(entries, ModItems.FIREWORK_SWORD);
                            addAll(entries, ModItems.GLASS_SWORD);
                            addAll(entries, ModItems.TOY_BOW);
                            addAll(entries, ModItems.MECHANICAL_SWORD);
                            addAll(entries, ModItems.SWORD_OF_DYING_STAR);
                        }))
                        .build()
        );
    }

    public static void registerItemGroups() {
        SatisfyingWeapons.LOGGER.info("Registering item groups");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((entries -> {
            entries.add(ModItems.UNFULFILLED_WISH);
        }));
    }

    private static void addAll(ItemGroup.Entries entries, List<Item> items) {
        for (Item item : items) {
            entries.add(item);
        }
    }
}
