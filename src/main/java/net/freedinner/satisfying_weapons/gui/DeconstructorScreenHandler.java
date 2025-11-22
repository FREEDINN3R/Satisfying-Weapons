package net.freedinner.satisfying_weapons.gui;

import net.freedinner.satisfying_weapons.block.ModBlocks;
import net.freedinner.satisfying_weapons.datagen.ModTags;
import net.freedinner.satisfying_weapons.item.ModItems;
import net.freedinner.satisfying_weapons.item.custom.IUpgradeableWeapon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

public class DeconstructorScreenHandler extends ScreenHandler {
    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 29;
    private static final int HOTBAR_START = 29;
    private static final int HOTBAR_END = 38;
    private final Inventory result = new CraftingResultInventory();
    final Inventory input = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            DeconstructorScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;

    public DeconstructorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public DeconstructorScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.DECONSTRUCTOR_SCREEN_HANDLER, syncId);

        this.context = context;

        this.addSlot(new Slot(this.input, 0, 42, 40) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(ModTags.ALL_MOD_WEAPONS);
            }
        });

        this.addSlot(new Slot(this.result, 0, 118, 40) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                DeconstructorScreenHandler.this.input.setStack(0, ItemStack.EMPTY);
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);

        if (inventory == this.input) {
            this.updateResult();
        }
    }

    private void updateResult() {
        ItemStack inputStack = this.input.getStack(0);

        if (inputStack.isEmpty() || !(inputStack.getItem() instanceof IUpgradeableWeapon weapon)) {
            this.result.setStack(0, ItemStack.EMPTY);
        }
        else {
            this.result.setStack(0, this.deconstruct(weapon));
        }

        this.sendContentUpdates();
    }

    private ItemStack deconstruct(IUpgradeableWeapon weapon) {
        int shardCount = switch (weapon.getRarityMaterial()) {
            case RARE -> 2;
            case EPIC -> 5;
            case LEGENDARY -> 12;
        };

        shardCount *= weapon.getLevel();

        return new ItemStack(ModItems.UNFULFILLED_WISH, shardCount);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.input));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.DECONSTRUCTOR);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int clickedSlotId) {
        Slot clickedSlot = this.slots.get(clickedSlotId);

        if (!clickedSlot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = clickedSlot.getStack();
        ItemStack resultingStack = stackInSlot.copy();

        ItemStack inputSlotStack = this.input.getStack(INPUT_ID);

        if (clickedSlotId == OUTPUT_ID) {
            if (!this.insertItem(stackInSlot, INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onQuickTransfer(stackInSlot, resultingStack);
        }
        else if (clickedSlotId != INPUT_ID) {
            if (!inputSlotStack.isEmpty()) {
                if (clickedSlotId >= INVENTORY_START && clickedSlotId < INVENTORY_END) {
                    if (!this.insertItem(stackInSlot, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (clickedSlotId >= HOTBAR_START && clickedSlotId < HOTBAR_END && !this.insertItem(stackInSlot, INVENTORY_START, INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.insertItem(stackInSlot, INPUT_ID, INPUT_ID + 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (!this.insertItem(stackInSlot, INVENTORY_START, HOTBAR_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            clickedSlot.setStack(ItemStack.EMPTY);
        } else {
            clickedSlot.markDirty();
        }

        if (stackInSlot.getCount() == resultingStack.getCount()) {
            return ItemStack.EMPTY;
        }

        clickedSlot.onTakeItem(player, stackInSlot);

        return resultingStack;
    }
}

