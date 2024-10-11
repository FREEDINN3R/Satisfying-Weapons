package net.freedinner.satisfying_weapons.item;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.custom.UnfulfilledWishItem;
import net.freedinner.satisfying_weapons.item.custom.WishingStarItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item UNFULFILLED_WISH = register("unfulfilled_wish",
            new UnfulfilledWishItem(new Item.Settings().rarity(Rarity.RARE).fireproof()));
    public static final Item WISHING_STAR = register("wishing_star",
            new WishingStarItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, SatisfyingWeapons.id(name), item);
    }

    public static void registerItems() {
        SatisfyingWeapons.LOGGER.info("Registering items");
    }
}
