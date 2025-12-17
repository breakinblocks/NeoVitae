package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.blockentity.TeleposerTile;
import com.breakinblocks.neovitae.common.item.ITeleposerFocus;

public class TeleposerMenu extends AbstractTileMenu<TeleposerTile> {

    private static final int TILE_SLOTS = 1; // Just the focus slot

    public TeleposerMenu(int containerId, Inventory playerInventory, TeleposerTile tile) {
        super(BMMenus.TELEPOSER.get(), containerId, tile, TILE_SLOTS);

        this.addSlot(new SlotItemHandler(tile.inv, TeleposerTile.FOCUS_SLOT, 80, 15) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ITeleposerFocus;
            }
        });

        // Player inventory and hotbar (compact layout)
        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory, 39, 97);
    }

    public TeleposerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (TeleposerTile) playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot) {
        if (index == 0) {
            // Focus slot to player
            if (!moveToPlayer(slotStack, true)) {
                return false;
            }
            slot.onQuickCraft(slotStack, originalCopy);
        } else if (isPlayerSlot(index)) {
            // Player to focus slot (only ITeleposerFocus items)
            if (slotStack.getItem() instanceof ITeleposerFocus) {
                if (!moveToTileSlots(slotStack, 0, 1)) {
                    return false;
                }
            }
            // Non-focus items just don't move (return true to continue cleanup)
        }
        return true;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return tile.stillValid(playerIn);
    }
}
