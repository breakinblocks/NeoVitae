// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.util.Utils;

import java.util.Iterator;
import java.util.List;

/**
 * Blacklist filter implementation. Blocks items that match the filter list.
 */
public class BlacklistItemFilter implements IItemFilter {

    protected List<IFilterKey> requestList;
    protected BlockEntity accessedTile;
    protected ResourceHandler<ItemResource> itemHandler;

    @Override
    public BlockPos getNodePos() {
        return accessedTile != null ? accessedTile.getBlockPos() : null;
    }

    @Override
    public void initializeFilter(List<IFilterKey> filteredList, BlockEntity tile, ResourceHandler<ItemResource> itemHandler, boolean isFilterOutput) {
        this.accessedTile = tile;
        this.itemHandler = itemHandler;

        if (isFilterOutput) {
            requestList = filteredList;

            for (int slot = 0; slot < itemHandler.size(); slot++) {
                ItemStack checkedStack = stackAt(slot);
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

            for (int slot = 0; slot < itemHandler.size(); slot++) {
                ItemStack checkedStack = stackAt(slot);
                if (checkedStack.isEmpty()) continue;

                int stackSize = checkedStack.getCount();

                for (IFilterKey filterStack : filteredList) {
                    if (doStacksMatch(filterStack, checkedStack)) {
                        filterStack.grow(stackSize);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack transferStackThroughOutputFilter(ItemStack inputStack) {
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
        for (int slot = 0; slot < itemHandler.size(); slot++) {
            ItemStack inputStack = stackAt(slot);
            if (inputStack.isEmpty()) continue;

            int simulated = simulateExtract(slot, inputStack.getCount());
            if (simulated <= 0) continue;

            int allowedAmount = Math.min(inputStack.getCount(), maxTransfer);

            for (IFilterKey filterStack : requestList) {
                if (doStacksMatch(filterStack, inputStack)) {
                    continue slots;
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

            extractCommitted(slot, changeAmount);

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

    private ItemStack stackAt(int slot) {
        ItemResource r = itemHandler.getResource(slot);
        return r.isEmpty() ? ItemStack.EMPTY : r.toStack(itemHandler.getAmountAsInt(slot));
    }

    private int simulateExtract(int slot, int amount) {
        ItemResource r = itemHandler.getResource(slot);
        if (r.isEmpty()) return 0;
        try (Transaction tx = Transaction.openRoot()) {
            return itemHandler.extract(slot, r, amount, tx);
        }
    }

    private void extractCommitted(int slot, int amount) {
        ItemResource r = itemHandler.getResource(slot);
        if (r.isEmpty()) return;
        try (Transaction tx = Transaction.openRoot()) {
            itemHandler.extract(slot, r, amount, tx);
            tx.commit();
        }
    }

    @Override
    public boolean doesStackPassFilter(ItemStack testStack) {
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
