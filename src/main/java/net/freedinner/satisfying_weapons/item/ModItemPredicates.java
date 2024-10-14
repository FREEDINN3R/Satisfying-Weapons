package net.freedinner.satisfying_weapons.item;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.item.custom.WishingStarItem;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;

public class ModItemPredicates {
    static {
        registerItemPredicate(ModItems.WISHING_STAR, "wishing", (itemStack, clientWorld, entity, seed) -> {
            if (entity != null && entity.isUsingItem() && entity.getActiveItem() == itemStack) {
                return 1;
            }

            return 0;
        });

        registerItemPredicate(ModItems.WISHING_STAR, "wish_progress", (itemStack, clientWorld, entity, seed) -> {
            if (entity != null && entity.isUsingItem() && entity.getActiveItem() == itemStack) {
                return WishingStarItem.getWishProgress(entity.getItemUseTime());
            }

            return 0;
        });
    }

    private static void registerItemPredicate(Item item, String name, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, SatisfyingWeapons.id(name), provider);
    }

    public static void registerItemPredicates() {
        SatisfyingWeapons.LOGGER.info("Registering item predicates");
    }
}
