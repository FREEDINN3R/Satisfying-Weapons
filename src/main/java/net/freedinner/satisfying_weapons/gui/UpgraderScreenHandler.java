package net.freedinner.satisfying_weapons.gui;

import net.freedinner.satisfying_weapons.block.ModBlocks;
import net.freedinner.satisfying_weapons.datagen.ModTags;
import net.freedinner.satisfying_weapons.item.custom.FireworkSwordItem;
import net.freedinner.satisfying_weapons.item.custom.IUpgradeableWeapon;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;

public class UpgraderScreenHandler extends ForgingScreenHandler {
    public UpgraderScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public UpgraderScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.UPGRADER_SCREEN_HANDLER, syncId, inventory, context);
    }

    @Override
    protected ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.create().input(0, 27, 47, stack -> true).input(1, 76, 47, stack -> true).output(2, 134, 47).build();
    }

    @Override
    protected boolean canUse(BlockState state) {
        return canUse(this.context, player, ModBlocks.UPGRADER);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return true;
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        this.input.setStack(0, ItemStack.EMPTY);
        this.input.setStack(1, ItemStack.EMPTY);
    }

    @Override
    public void updateResult() {
        ItemStack primaryStack = this.input.getStack(0);
        ItemStack secondaryStack = this.input.getStack(1);

        if (primaryStack.isOf(Items.LAPIS_ORE) && secondaryStack.getItem() instanceof FireworkSwordItem) {
            this.output.setStack(0, new ItemStack(ModBlocks.BLOCK_OF_LOFS, 64));
            this.sendContentUpdates();
            return;
        }

        if (primaryStack.isEmpty() || secondaryStack.isEmpty() || !(primaryStack.getItem() instanceof IUpgradeableWeapon weapon)) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        if (weapon.isAtMaxLevel() || !secondaryStack.getItem().getClass().equals(weapon.getClass())) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        Item nextLevelWeapon = (Item) weapon.getNextLevel();
        assert nextLevelWeapon != null;
        ItemStack outputStack = new ItemStack(nextLevelWeapon);

        outputStack.setNbt(primaryStack.getNbt());

        this.output.setStack(0, outputStack);
        this.sendContentUpdates();
    }
}

