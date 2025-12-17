package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.item.routing.IFilterKey;
import com.breakinblocks.neovitae.util.Utils;

import java.util.Iterator;
import java.util.List;

/**
 * Blacklist filter implementation.
 * Blocks items that match the filter list.
 */
public class BlacklistItemFilter implements IItemFilter {

    protected List<IFilterKey> requestList;
    protected BlockEntity accessedTile;
    protected IItemHandler itemHandler;

    @Override
    public void initializeFilter(List<IFilterKey> filteredList, BlockEntity tile, IItemHandler itemHandler, boolean isFilterOutput) {
        this.accessedTile = tile;
        this.itemHandler = itemHandler;

        if (isFilterOutput) {
            requestList = filteredList;

            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                ItemStack checkedStack = itemHandler.getStackInSlot(slot);
                if (checkedStack.isEmpty()) continue;

                int stackSize = checkedStack.getCount();

                for (IFilterKey filterStack : requestList) {
                    if (filterStack.getCount() == 0) continue;

                    if (doStacksMatch(filterStack, checkedStack)) {
                        filterStack.setCount(Math.max(filterStack.getCount() - stackSize, 0));
                    }
                }
            }
        } else {
            requestList = filteredList;
            for (IFilterKey filterStack : requestList) {
                filterStack.setCount(filterStack.getCount() * -1);
            }

            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                ItemStack checkedStack = itemHandler.getStackInSlot(slot);
                if (checkedStack.isEmpty()) continue;

                int stackSize = checkedStack.getCount();

                for (IFilterKey filterStack : filteredList) {
                    if (doStacksMatch(filterStack, checkedStack)) {
                        filterStack.grow(stackSize);
                    }
                }
            }
        }
        // Note: Blacklist filter doesn't remove empty entries
    }

    @Override
    public ItemStack transferStackThroughOutputFilter(ItemStack inputStack) {
        // If the stack matches the blacklist, reject it
        for (IFilterKey filterStack : requestList) {
            if (doStacksMatch(filterStack, inputStack)) {
                return inputStack;
            }
        }

        int allowedAmount = inputStack.getCount();
        if (allowedAmount <= 0) {
            return inputStack;
        }

        ItemStack testStack = inputStack.copy();
        testStack.setCount(allowedAmount);
        ItemStack remainderStack = Utils.insertStackIntoTile(testStack, itemHandler);

        int changeAmount = allowedAmount - (remainderStack.isEmpty() ? 0 : remainderStack.getCount());
        testStack = inputStack.copy();
        testStack.shrink(changeAmount);

        Iterator<IFilterKey> itr = requestList.iterator();
        while (itr.hasNext()) {
            IFilterKey filterStack = itr.next();
            if (!doStacksMatch(filterStack, inputStack)) {
                filterStack.shrink(changeAmount);
                if (filterStack.isEmpty()) {
                    itr.remove();
                }
            }
        }

        if (accessedTile != null) {
            Level level = accessedTile.getLevel();
            BlockPos pos = accessedTile.getBlockPos();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }

        return testStack;
    }

    @Override
    public int transferThroughInputFilter(IItemFilter outputFilter, int maxTransfer) {
        int totalChange = 0;

        slots:
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack inputStack = itemHandler.getStackInSlot(slot);
            if (inputStack.isEmpty() || itemHandler.extractItem(slot, inputStack.getCount(), true).isEmpty()) {
                continue;
            }

            int allowedAmount = Math.min(inputStack.getCount(), maxTransfer);

            // Check if it matches the blacklist
            for (IFilterKey filterStack : requestList) {
                if (doStacksMatch(filterStack, inputStack)) {
                    continue slots; // Skip blacklisted items
                }
            }

            if (allowedAmount <= 0) {
                continue;
            }

            ItemStack testStack = inputStack.copy();
            testStack.setCount(allowedAmount);
            ItemStack remainderStack = outputFilter.transferStackThroughOutputFilter(testStack);
            int changeAmount = allowedAmount - (remainderStack.isEmpty() ? 0 : remainderStack.getCount());

            if (!remainderStack.isEmpty() && remainderStack.getCount() == allowedAmount) {
                continue;
            }

            itemHandler.extractItem(slot, changeAmount, false);

            Iterator<IFilterKey> itr = requestList.iterator();
            while (itr.hasNext()) {
                IFilterKey filterStack = itr.next();
                if (!doStacksMatch(filterStack, inputStack)) {
                    filterStack.shrink(changeAmount);
                }
            }

            if (accessedTile != null) {
                Level level = accessedTile.getLevel();
                BlockPos pos = accessedTile.getBlockPos();
                level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            }

            maxTransfer -= changeAmount;
            totalChange += changeAmount;
            if (maxTransfer <= 0) {
                return totalChange;
            }
        }

        return totalChange;
    }

    @Override
    public boolean doesStackPassFilter(ItemStack testStack) {
        // Blacklist: passes if it does NOT match
        for (IFilterKey filterStack : requestList) {
            if (doStacksMatch(filterStack, testStack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean doStacksMatch(IFilterKey filterStack, ItemStack testStack) {
        return filterStack.doesStackMatch(testStack);
    }

    @Override
    public void initializeFilter(List<IFilterKey> filteredList) {
        this.requestList = filteredList;
    }

    @Override
    public List<IFilterKey> getFilterList() {
        return this.requestList;
    }
}
