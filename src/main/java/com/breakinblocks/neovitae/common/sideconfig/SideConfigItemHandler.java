package com.breakinblocks.neovitae.common.sideconfig;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class SideConfigItemHandler implements IItemHandler {

    private final IItemHandler delegate;
    private final SlotSideConfig config;
    @Nullable
    private final Direction side;

    public SideConfigItemHandler(IItemHandler delegate, SlotSideConfig config, @Nullable Direction side) {
        this.delegate = delegate;
        this.config = config;
        this.side = side;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!isAllowed(slot)) return stack;
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!isAllowed(slot)) return ItemStack.EMPTY;
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return isAllowed(slot) && delegate.isItemValid(slot, stack);
    }

    private boolean isAllowed(int slot) {
        if (side == null) return true;
        return config.isAllowed(slot, side);
    }
}
