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
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.common.item.NVItems;

public class MasterRoutingNodeMenu extends AbstractContainerMenu {

    public final MasterRoutingNodeBlockEntity tile;
    private final ContainerData data;

    private static final int DATA_GENERAL_COUNT = 0;
    private static final int DATA_INPUT_COUNT = 1;
    private static final int DATA_OUTPUT_COUNT = 2;
    private static final int DATA_TICK_RATE = 3;
    private static final int DATA_MAX_TRANSFER_LO = 4;
    private static final int DATA_MAX_TRANSFER_HI = 5;
    private static final int DATA_ENERGY_RATE_LO = 6;
    private static final int DATA_ENERGY_RATE_HI = 7;
    private static final int DATA_ENERGY_CEILING_LO = 8;
    private static final int DATA_ENERGY_CEILING_HI = 9;
    private static final int DATA_SIZE = 10;

    public MasterRoutingNodeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntitySafe(playerInventory, buf.readBlockPos()));
    }

    private static MasterRoutingNodeBlockEntity getBlockEntitySafe(Inventory playerInventory, net.minecraft.core.BlockPos pos) {
        if (playerInventory.player.level() == null) return null;
        if (playerInventory.player.level().getBlockEntity(pos) instanceof MasterRoutingNodeBlockEntity tile) {
            return tile;
        }
        return null;
    }

    public MasterRoutingNodeMenu(int containerId, Inventory playerInventory, MasterRoutingNodeBlockEntity tile) {
        super(NVMenus.MASTER_ROUTING_NODE.get(), containerId);
        this.tile = tile;

        if (tile != null && !playerInventory.player.level().isClientSide) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    return switch (index) {
                        case DATA_GENERAL_COUNT -> tile.getGeneralNodeCount();
                        case DATA_INPUT_COUNT -> tile.getInputNodeCount();
                        case DATA_OUTPUT_COUNT -> tile.getOutputNodeCount();
                        case DATA_TICK_RATE -> RoutingNodeHelper.getEffectiveTickRate(
                                tile.getBlockState().getBlock(),
                                tile.getItem(MasterRoutingNodeBlockEntity.SLOT_SPEED_UPGRADE).getCount()
                        );
                        case DATA_MAX_TRANSFER_LO -> low(tile.getMaxTransfer());
                        case DATA_MAX_TRANSFER_HI -> high(tile.getMaxTransfer());
                        case DATA_ENERGY_RATE_LO -> low(tile.getConfiguredEnergyRate());
                        case DATA_ENERGY_RATE_HI -> high(tile.getConfiguredEnergyRate());
                        case DATA_ENERGY_CEILING_LO -> low(tile.getUpgradeEnergyCeiling());
                        case DATA_ENERGY_CEILING_HI -> high(tile.getUpgradeEnergyCeiling());
                        default -> 0;
                    };
                }

                @Override
                public void set(int index, int value) {
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

        if (tile != null) {
            this.addSlot(new UpgradeSlot(tile, MasterRoutingNodeBlockEntity.SLOT_STACK_UPGRADE, 62, 15));
            this.addSlot(new UpgradeSlot(tile, MasterRoutingNodeBlockEntity.SLOT_SPEED_UPGRADE, 98, 15));
        }

        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, 64, 122);
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
        return combine(DATA_MAX_TRANSFER_LO, DATA_MAX_TRANSFER_HI);
    }

    public int getTickRate() {
        return data.get(DATA_TICK_RATE);
    }

    public int getEnergyRate() {
        return combine(DATA_ENERGY_RATE_LO, DATA_ENERGY_RATE_HI);
    }

    public int getEnergyCeiling() {
        return combine(DATA_ENERGY_CEILING_LO, DATA_ENERGY_CEILING_HI);
    }

    private int combine(int loIndex, int hiIndex) {
        return ((data.get(hiIndex) & 0xFFFF) << 16) | (data.get(loIndex) & 0xFFFF);
    }

    private static int low(int value) {
        return value & 0xFFFF;
    }

    private static int high(int value) {
        return (value >>> 16) & 0xFFFF;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 2) {
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
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

    private static class UpgradeSlot extends Slot {
        private final int upgradeSlot;

        public UpgradeSlot(MasterRoutingNodeBlockEntity container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.upgradeSlot = slot;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (upgradeSlot == MasterRoutingNodeBlockEntity.SLOT_SPEED_UPGRADE) {
                return stack.is(NVItems.MASTER_NODE_UPGRADE_SPEED.get());
            }
            return stack.is(NVItems.MASTER_NODE_UPGRADE.get());
        }
    }
}
