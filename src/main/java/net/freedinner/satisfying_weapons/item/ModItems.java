package net.freedinner.satisfying_weapons.item;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.custom.*;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModItems {
    // --- GENERIC ITEMS --- //
    public static final Item UNFULFILLED_WISH = register("unfulfilled_wish",
            new Item(new Item.Settings().rarity(Rarity.RARE).fireproof()));
    public static final Item WISHING_STAR = register("wishing_star",
            new WishingStarItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).fireproof()));
    public static final Item BROKEN_GLASS_SWORD = register("broken_glass_sword",
            new Item(new Item.Settings()));

    // --- WEAPONS --- //
    public static final List<Item> FIREWORK_SWORD = registerUpgradeableWeapon("firework_sword",
            FireworkSwordItem::new, ModToolMaterial.RARE, 5, new Item.Settings());
    public static final List<Item> GLASS_SWORD = registerUpgradeableWeapon("glass_sword",
            GlassSwordItem::new, ModToolMaterial.RARE, 5, new Item.Settings());

    public static final List<Item> TOY_BOW = registerUpgradeableWeapon("toy_bow",
            ToyBowItem::new, ModToolMaterial.EPIC, 5, new Item.Settings());
    public static final List<Item> SWORD_OF_DYING_STAR = registerUpgradeableWeapon("sword_of_dying_star",
            DyingStarSwordItem::new, ModToolMaterial.LEGENDARY, 5, new Item.Settings());

    // --- TECHNICAL ITEMS --- //
    public static final Item BLACK_HOLE = register("black_hole",
            new Item(new Item.Settings()));


    @SuppressWarnings("unchecked")
    private static <T extends Item & IUpgradeableWeapon> List<Item> registerUpgradeableWeapon(
            String name, BlockStateVariantMap.QuadFunction<ModToolMaterial, Item.Settings, Integer, T, T> constructor,
            ModToolMaterial material, int maxLevel, Item.Settings settings
    ) {
        List<Item> list = new ArrayList<>();

        T nextLevelWeapon = null;
        int i = maxLevel;

        do {
            // Recursively adding weapon levels, in order to properly assign nextLevelWeapon
            T currentInstance = (T) register(name + "_l" + i, constructor.apply(material, settings.fireproof(), i, nextLevelWeapon));
            list.add(currentInstance);
            nextLevelWeapon = currentInstance;

            i--;
        }
        while (i > 0);

        Collections.reverse(list);
        return list;
    }


    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, SatisfyingWeapons.id(name), item);
    }

    public static void registerItems() {
        SatisfyingWeapons.LOGGER.info("Registering items");
    }
}
