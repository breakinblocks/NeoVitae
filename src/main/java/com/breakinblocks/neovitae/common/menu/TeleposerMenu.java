package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import com.breakinblocks.neovitae.common.blockentity.TeleposerBlockEntity;
import com.breakinblocks.neovitae.common.item.ITeleposerFocus;

public class TeleposerMenu extends AbstractBlockEntityMenu<TeleposerBlockEntity> {

    private static final int TILE_SLOTS = 1; // Just the focus slot

    public TeleposerMenu(int containerId, Inventory playerInventory, TeleposerBlockEntity tile) {
        super(NVMenus.TELEPOSER.get(), containerId, tile, TILE_SLOTS);

        this.addSlot(new ResourceHandlerSlot(tile.inv, tile.inv::set, TeleposerBlockEntity.FOCUS_SLOT, 80, 15) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ITeleposerFocus;
            }
        });

        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, 39, 97);
    }

    public TeleposerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (TeleposerBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot) {
        if (index == 0) {
            if (!moveToPlayer(slotStack, true)) {
                return false;
            }
            slot.onQuickCraft(slotStack, originalCopy);
        } else if (isPlayerSlot(index)) {
            if (slotStack.getItem() instanceof ITeleposerFocus) {
                if (!moveToTileSlots(slotStack, 0, 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return tile.stillValid(playerIn);
    }
}
