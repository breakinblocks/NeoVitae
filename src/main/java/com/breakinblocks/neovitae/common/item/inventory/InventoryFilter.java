package com.breakinblocks.neovitae.common.item.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

/**
 * ItemStackHandler for filter ghost slots.
 * Each slot holds a single item as a ghost reference for filtering.
 */
public class InventoryFilter extends ItemStackHandler {

    public InventoryFilter(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return 1;
    }
}
