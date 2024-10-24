package net.freedinner.satisfying_weapons.item;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.custom.FireworkSwordItem;
import net.freedinner.satisfying_weapons.item.custom.WishingStarItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item UNFULFILLED_WISH = register("unfulfilled_wish",
            new Item(new Item.Settings().rarity(Rarity.RARE).fireproof()));
    public static final Item WISHING_STAR = register("wishing_star",
            new WishingStarItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    public static final Item FIREWORK_SWORD = register("firework_sword",
            new FireworkSwordItem(ToolMaterials.IRON, 4, -2.4f, new Item.Settings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, SatisfyingWeapons.id(name), item);
    }

    public static void registerItems() {
        SatisfyingWeapons.LOGGER.info("Registering items");
    }
}
