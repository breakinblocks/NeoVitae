package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeTile;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.common.item.BMItems;

/**
 * Menu for the Master Routing Node.
 * Contains upgrade slots for stack size and speed upgrades.
 */
public class MasterRoutingNodeMenu extends AbstractContainerMenu {

    public final MasterRoutingNodeTile tile;
    private final ContainerData data;

    // Data indices
    private static final int DATA_GENERAL_COUNT = 0;
    private static final int DATA_INPUT_COUNT = 1;
    private static final int DATA_OUTPUT_COUNT = 2;
    private static final int DATA_MAX_TRANSFER = 3;
    private static final int DATA_TICK_RATE = 4;
    private static final int DATA_SIZE = 5;

    public MasterRoutingNodeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntitySafe(playerInventory, buf.readBlockPos()));
    }

    private static MasterRoutingNodeTile getBlockEntitySafe(Inventory playerInventory, net.minecraft.core.BlockPos pos) {
        if (playerInventory.player.level() == null) return null;
        if (playerInventory.player.level().getBlockEntity(pos) instanceof MasterRoutingNodeTile tile) {
            return tile;
        }
        return null;
    }

    public MasterRoutingNodeMenu(int containerId, Inventory playerInventory, MasterRoutingNodeTile tile) {
        super(BMMenus.MASTER_ROUTING_NODE.get(), containerId);
        this.tile = tile;

        if (tile != null && !playerInventory.player.level().isClientSide) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    return switch (index) {
                        case DATA_GENERAL_COUNT -> tile.getGeneralNodeCount();
                        case DATA_INPUT_COUNT -> tile.getInputNodeCount();
                        case DATA_OUTPUT_COUNT -> tile.getOutputNodeCount();
                        case DATA_MAX_TRANSFER -> tile.getMaxTransfer();
                        case DATA_TICK_RATE -> RoutingNodeHelper.getEffectiveTickRate(
                                tile.getBlockState().getBlock(),
                                tile.getItem(MasterRoutingNodeTile.SLOT_SPEED_UPGRADE).getCount()
                        );
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
                    // Read-only data
                }

                @Override
                public int getCount() {
                    return DATA_SIZE;
                }
            };
        } else {
            this.data = new SimpleContainerData(DATA_SIZE);
        }

        addDataSlots(this.data);

        // Add upgrade slots only if tile exists - positions match 1.20.1
        if (tile != null) {
            this.addSlot(new UpgradeSlot(tile, MasterRoutingNodeTile.SLOT_STACK_UPGRADE, 62, 15));
            this.addSlot(new UpgradeSlot(tile, MasterRoutingNodeTile.SLOT_SPEED_UPGRADE, 98, 15));
        }

        // Player inventory and hotbar - positions match 1.20.1
        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, 39, 97);
    }

    public int getGeneralNodeCount() {
        return data.get(DATA_GENERAL_COUNT);
    }

    public int getInputNodeCount() {
        return data.get(DATA_INPUT_COUNT);
    }

    public int getOutputNodeCount() {
        return data.get(DATA_OUTPUT_COUNT);
    }

    public int getMaxTransfer() {
        return data.get(DATA_MAX_TRANSFER);
    }

    public int getTickRate() {
        return data.get(DATA_TICK_RATE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            // 0-1 are upgrade slots, 2-37 are player inventory
            if (index < 2) {
                // Move from upgrade slots to player inventory
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory to upgrade slots
                if (!this.moveItemStackTo(stackInSlot, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return tile != null && tile.stillValid(player);
    }

    /**
     * Slot that only accepts routing upgrade items.
     */
    private static class UpgradeSlot extends Slot {
        public UpgradeSlot(MasterRoutingNodeTile container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Accept Node Router items as upgrades
            return stack.is(BMItems.NODE_ROUTER.get());
        }
    }
}
