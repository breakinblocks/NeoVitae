package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;

public class TabulaVitaeMenu extends AbstractBlockEntityMenu<TabulaVitaeBlockEntity> {

    private static final int TILE_SLOTS = 8; // 6 input + 1 orb + 1 output

    public TabulaVitaeMenu(int containerId, Inventory playerInventory, TabulaVitaeBlockEntity tile) {
        super(NVMenus.TABULA_VITAE.get(), containerId, tile, TILE_SLOTS);

        this.addSlot(new SlotItemHandler(tile.inv, 0, 62, 15));
        this.addSlot(new SlotItemHandler(tile.inv, 1, 80, 51));
        this.addSlot(new SlotItemHandler(tile.inv, 2, 62, 87));
        this.addSlot(new SlotItemHandler(tile.inv, 3, 26, 87));
        this.addSlot(new SlotItemHandler(tile.inv, 4, 8, 51));
        this.addSlot(new SlotItemHandler(tile.inv, 5, 26, 15));
        this.addSlot(new SlotItemHandler(tile.inv, TabulaVitaeBlockEntity.ORB_SLOT, 143, 24) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BloodOrbItem;
            }
        });
        this.addSlot(new SlotItemHandler(tile.inv, TabulaVitaeBlockEntity.OUTPUT_SLOT, 44, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory,
                MenuSlotHelper.INV_Y_205, MenuSlotHelper.HOTBAR_Y_205);
    }

    public TabulaVitaeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (TabulaVitaeBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot) {
        if (index == TabulaVitaeBlockEntity.OUTPUT_SLOT) {
            if (!moveToPlayer(slotStack, true)) {
                return false;
            }
            slot.onQuickCraft(slotStack, originalCopy);
        } else if (isPlayerSlot(index)) {
            if (slotStack.getItem() instanceof BloodOrbItem) {
                if (!moveToTileSlots(slotStack, 6, 7)) {
                    return false;
                }
            } else if (!moveToTileSlots(slotStack, 0, 6)) {
                return false;
            }
        } else if (!moveToPlayer(slotStack, false)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), player, NVBlocks.TABULA_VITAE.block().get());
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId <= TabulaVitaeBlockEntity.OUTPUT_SLOT) {
            Slot slot = this.getSlot(slotId);
            if (!slot.hasItem() && this.getCarried().isEmpty()) {
                tile.activeSlot = (tile.activeSlot == slotId) ? -1 : slotId;
            }
        }
        super.clicked(slotId, dragType, clickType, player);
    }
}
