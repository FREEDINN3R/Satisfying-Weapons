package net.freedinner.satisfying_weapons.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

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
}
