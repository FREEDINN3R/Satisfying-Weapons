package net.freedinner.satisfying_weapons.item;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.custom.FireworkSword;
import net.freedinner.satisfying_weapons.item.custom.IUpgradeableWeapon;
import net.freedinner.satisfying_weapons.item.custom.WishingStarItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final Item UNFULFILLED_WISH = register("unfulfilled_wish",
            new Item(new Item.Settings().rarity(Rarity.RARE).fireproof()));
    public static final Item WISHING_STAR = register("wishing_star",
            new WishingStarItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final List<Item> FIREWORK_SWORD = registerUpgradeableWeapon("firework_sword",
            FireworkSword::new, ModToolMaterial.RARE, new Item.Settings());

    @SuppressWarnings("unchecked")
    private static <T extends Item & IUpgradeableWeapon> List<Item> registerUpgradeableWeapon(
            String name, TriFunction<ToolMaterial, Item.Settings, Integer, T> constructor,
            ToolMaterial material, Item.Settings settings
    ) {
        List<Item> list = new ArrayList<>();
        T newInstance;
        int i = 0;

        do {
            i++;
            newInstance = (T) register(name + "_l" + i, constructor.apply(material, settings, i));
            list.add(newInstance);
        }
        while (i < newInstance.getMaxLevel());

        return list;
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, SatisfyingWeapons.id(name), item);
    }

    public static void registerItems() {
        SatisfyingWeapons.LOGGER.info("Registering items");
    }
}
