package net.freedinner.satisfying_weapons.event.handler;

import net.freedinner.satisfying_weapons.event.custom.CustomLivingEntityEvents;
import net.freedinner.satisfying_weapons.item.custom.CrimsonKatanaItem;
import net.freedinner.satisfying_weapons.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AfterDamageCrimsonKatana implements CustomLivingEntityEvents.AfterDamage {
    @Override
    public void afterDamage(LivingEntity entity, DamageSource source) {
        Entity attacker = source.getAttacker();

        if (!(attacker instanceof PlayerEntity playerAttacker)) {
            return;
        }

        CrimsonKatanaItem katanaItem = searchHotbarForKatana(playerAttacker);

        if (katanaItem == null) {
            return;
        }

        ArrayList<CrimsonKatanaItem.DoT> possibleDots = CrimsonKatanaItem.DoT.getDotsForLevel(katanaItem.getLevel());
        ArrayList<CrimsonKatanaItem.DoT> chosenDots = new ArrayList<>();

        int count = (katanaItem.getLevel() >= 4) ? 2 : 1;
        for (int i = 0; i < count; i++) {
            chosenDots.add(MathUtils.getRandomElement(possibleDots, true));
        }

        for (CrimsonKatanaItem.DoT dotEffect : chosenDots) {
            dotEffect.inflictOn(entity);
        }
    }

    @Nullable
    private static CrimsonKatanaItem searchHotbarForKatana(PlayerEntity playerAttacker) {
        PlayerInventory inventory = playerAttacker.getInventory();
        CrimsonKatanaItem katanaItem = null;

        ItemStack offhandStack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
        if (offhandStack.getItem() instanceof CrimsonKatanaItem foundKatanaItem) {
            katanaItem = foundKatanaItem;
        }

        for (int i = 0; PlayerInventory.isValidHotbarIndex(i); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof CrimsonKatanaItem foundKatanaItem
            && (katanaItem == null || katanaItem.getLevel() < foundKatanaItem.getLevel())) {
                katanaItem = foundKatanaItem;
            }
        }

        return katanaItem;
    }
}
