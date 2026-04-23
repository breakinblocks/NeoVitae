package com.breakinblocks.neovitae.common.menu;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class GhostItemHandler extends ItemStacksResourceHandler {

    public GhostItemHandler(NonNullList<ItemStack> initial) {
        super(initial);
    }

    public GhostItemHandler(int size) {
        super(size);
    }

    public ItemStack getStackInSlot(int slot) {
        ItemResource r = getResource(slot);
        return r.isEmpty() ? ItemStack.EMPTY : r.toStack(getAmountAsInt(slot));
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        set(slot, ItemResource.of(stack), stack.getCount());
    }

    public int getSlots() { return size(); }
}
