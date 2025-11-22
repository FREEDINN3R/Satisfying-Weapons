package net.freedinner.satisfying_weapons.item;

import net.freedinner.satisfying_weapons.SatisfyingWeapons;
import net.freedinner.satisfying_weapons.entity.custom.EnergyDischargeEntity;
import net.freedinner.satisfying_weapons.item.custom.GlassSwordItem;
import net.freedinner.satisfying_weapons.item.custom.WishingStarItem;
import net.freedinner.satisfying_weapons.util.data.IPlayerDataSaver;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;

import java.util.List;

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

        registerItemPredicate(ModItems.GLASS_SWORD, "glass_state", (itemStack, clientWorld, entity, seed) -> {
            return GlassSwordItem.getGlassState(itemStack).ordinal() / 2f; // returns 0, 0.5, or 1
        });

        registerItemPredicate(ModItems.MECHANICAL_GREATSWORD, "charging", (itemStack, clientWorld, entity, seed) -> {
            if (entity != null && entity.isUsingItem() && entity.getActiveItem() == itemStack && entity.getActiveHand() == Hand.MAIN_HAND) {
                return 1;
            }

            return 0;
        });

        registerItemPredicate(ModItems.MECHANICAL_GREATSWORD, "charge_level", (itemStack, clientWorld, entity, seed) -> {
            if (entity instanceof PlayerEntity player) {
                return ((IPlayerDataSaver) player).getGreatswordChargeLevel() / EnergyDischargeEntity.CHARGE_TICKS * 0.1f;
            }

            return 0;
        });

        ClampedModelPredicateProvider pulling = (itemStack, world, entity, seed) -> {
            if (entity != null && entity.isUsingItem() && entity.getActiveItem() == itemStack) {
                return 1;
            }

            return 0;
        };

        ClampedModelPredicateProvider pull = (itemsStack, world, entity, seed) -> {
            if (entity != null && entity.getActiveItem() == itemsStack) {
                return BowItem.getPullProgress(entity.getItemUseTime());
            }

            return 0;
        };

        registerItemPredicate(ModItems.TOY_BOW, "pulling", pulling);
        registerItemPredicate(ModItems.TOY_BOW,"pull", pull);
    }

    private static void registerItemPredicate(List<Item> items, String name, ClampedModelPredicateProvider provider) {
        for (Item item : items) {
            registerItemPredicate(item, name, provider);
        }
    }

    private static void registerItemPredicate(Item item, String name, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(item, SatisfyingWeapons.id(name), provider);
    }

    public static void registerItemPredicates() {
        SatisfyingWeapons.LOGGER.info("Registering item predicates");
    }
}
