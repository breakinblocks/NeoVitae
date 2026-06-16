package com.breakinblocks.neovitae.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.items.SlotItemHandler;
import com.breakinblocks.neovitae.common.advancement.NVCriteriaTriggers;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

public class HellfireForgeMenu extends AbstractBlockEntityMenu<HellfireForgeBlockEntity> {

    private static final int TILE_SLOTS = 6; // 4 input + 1 gem + 1 output

    private final Player owner;

    public HellfireForgeMenu(int containerId, Inventory playerInventory, HellfireForgeBlockEntity tile) {
        super(NVMenus.HELLFIRE_FORGE.get(), containerId, tile, TILE_SLOTS);

        this.owner = playerInventory.player;

        this.addSlot(new SlotItemHandler(tile.inv, 0, 8, 15));
        this.addSlot(new SlotItemHandler(tile.inv, 1, 80, 15));
        this.addSlot(new SlotItemHandler(tile.inv, 2, 8, 87));
        this.addSlot(new SlotItemHandler(tile.inv, 3, 80, 87));

        this.addSlot(new SlotItemHandler(tile.inv, HellfireForgeBlockEntity.GEM_SLOT, 152, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.has(NVDataComponents.SPIRITUS_AMOUNT);
            }
        });

        this.addSlot(new SlotItemHandler(tile.inv, HellfireForgeBlockEntity.OUTPUT_SLOT, 44, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                awardBloodMending(player, stack);
                super.onTake(player, stack);
            }
        });

        MenuSlotHelper.addPlayerInventory(this::addSlot, playerInventory,
                MenuSlotHelper.INV_Y_205, MenuSlotHelper.HOTBAR_Y_205);

        this.addDataSlots(tile.dataAccess);
    }

    public HellfireForgeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, (HellfireForgeBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    protected boolean handleQuickMoveStack(int index, ItemStack slotStack, ItemStack originalCopy, Slot slot) {
        if (index == HellfireForgeBlockEntity.OUTPUT_SLOT) {
            if (!moveToPlayer(slotStack, true)) {
                return false;
            }
            slot.onQuickCraft(slotStack, originalCopy);
            awardBloodMending(owner, originalCopy);
        } else if (isPlayerSlot(index)) {
            if (slotStack.has(NVDataComponents.SPIRITUS_AMOUNT)) {
                if (!moveToTileSlots(slotStack, 4, 5)) {
                    return false;
                }
            } else if (!moveToTileSlots(slotStack, 0, 4)) {
                return false;
            }
        } else if (!moveToPlayer(slotStack, false)) {
            return false;
        }
        return true;
    }

    private void awardBloodMending(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer sp && stack.has(NVDataComponents.BLOOD_MENDING.get())) {
            NVCriteriaTriggers.BLOOD_MENDING_CRAFTED.get().trigger(sp);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), player, NVBlocks.HELLFIRE_FORGE.block().get());
    }
}
