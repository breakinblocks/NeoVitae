package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.blockentity.ARCTile;

public class ARCMenu extends AbstractTileMenu<ARCTile> {

    // 4 input slots (bucket in, bucket out, tool, input) + 5 output slots
    private static final int TILE_SLOTS = 4 + ARCTile.NUM_OUTPUTS;

    public ARCMenu(int containerId, Inventory playerInventory, ARCTile tile) {
        super(BMMenus.ARC.get(), containerId, tile, TILE_SLOTS);

        this.addSlot(new SlotItemHandler(ARCTile.getItemHandler(tile, null), ARCTile.INPUT_BUCKET_SLOT, 8, 18));
        this.addSlot(new SlotItemHandler(ARCTile.getItemHandler(tile, null), ARCTile.OUTPUT_BUCKET_SLOT, 152, 90));
        this.addSlot(new SlotItemHandler(ARCTile.getItemHandler(tile, null), ARCTile.TOOL_SLOT, 35, 54));
        this.addSlot(new SlotItemHandler(ARCTile.getItemHandler(tile, null), ARCTile.INPUT_SLOT, 71, 18));

        for (int i = 0; i < ARCTile.NUM_OUTPUTS; i++) {
            this.addSlot(new SlotItemHandler(ARCTile.getItemHandler(tile, null), ARCTile.OUTPUT_SLOT + i, 116, 18 + i * 18) {
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

        // Player inventory and hotbar
        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory,
                MenuSlotHelper.INV_Y_208, MenuSlotHelper.HOTBAR_Y_208);
    }

    public ARCMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (ARCTile) playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot) {
        if (isTileSlot(index)) {
            // From machine to player
            if (!moveToPlayer(slotStack, false)) {
                return false;
            }
        } else {
            // From player to machine
            if (!moveToTileSlots(slotStack, 0, playerSlotsStart)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.NULL, player, BMBlocks.ARC_BLOCK.block().get());
    }
}
