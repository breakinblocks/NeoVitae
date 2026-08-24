package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;

public class AthanorMenu extends AbstractBlockEntityMenu<AthanorBlockEntity> {

    private static final int TILE_SLOTS = 1 + AthanorBlockEntity.NUM_INPUTS + 2 + AthanorBlockEntity.NUM_OUTPUTS;

    private final DataSlot idleData = new DataSlot() {
        private int value;

        @Override
        public int get() {
            return tile != null ? tile.getIdleReason().ordinal() : value;
        }

        @Override
        public void set(int newValue) {
            value = newValue;
            if (tile != null) {
                tile.setIdleReasonFromNetwork(newValue);
            }
        }
    };

    public AthanorMenu(int containerId, Inventory playerInventory, AthanorBlockEntity tile) {
        super(NVMenus.ARC.get(), containerId, tile, TILE_SLOTS);

        this.addSlot(new SlotItemHandler(AthanorBlockEntity.getItemHandler(tile, null), AthanorBlockEntity.TOOL_SLOT, 35, 55));

        // 6 input slots in a 3x2 grid
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIdx = AthanorBlockEntity.INPUT_START + row * 3 + col;
                this.addSlot(new SlotItemHandler(AthanorBlockEntity.getItemHandler(tile, null), slotIdx, 33 + col * 18, 14 + row * 18));
            }
        }

        this.addSlot(new SlotItemHandler(AthanorBlockEntity.getItemHandler(tile, null), AthanorBlockEntity.INPUT_BUCKET_SLOT, 8, 18));
        this.addSlot(new SlotItemHandler(AthanorBlockEntity.getItemHandler(tile, null), AthanorBlockEntity.OUTPUT_BUCKET_SLOT, 152, 90));

        for (int i = 0; i < AthanorBlockEntity.NUM_OUTPUTS; i++) {
            this.addSlot(new SlotItemHandler(AthanorBlockEntity.getItemHandler(tile, null), AthanorBlockEntity.OUTPUT_SLOT + i, 116, 18 + i * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    stack.onCraftedBy(player.level(), player, stack.getCount());
                    super.onTake(player, stack);
                }
            });
        }

        this.addDataSlot(idleData);

        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory,
                MenuSlotHelper.INV_Y_208, MenuSlotHelper.HOTBAR_Y_208);
    }

    public AthanorBlockEntity.IdleReason getIdleReason() {
        return AthanorBlockEntity.IdleReason.byIndex(idleData.get());
    }

    public AthanorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (AthanorBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot) {
        if (isTileSlot(index)) {
            if (!moveToPlayer(slotStack, false)) {
                return false;
            }
        } else {
            if (!moveToTileSlots(slotStack, 0, playerSlotsStart)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.NULL, player, NVBlocks.ATHANOR_BLOCK.block().get());
    }
}
