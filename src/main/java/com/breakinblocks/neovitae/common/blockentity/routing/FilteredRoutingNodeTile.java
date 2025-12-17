package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.util.Constants;

/**
 * Filtered routing node with a filter slot per direction and priority settings.
 */
public class FilteredRoutingNodeTile extends RoutingNodeTile implements Container, WorldlyContainer {

    protected NonNullList<ItemStack> items;
    private int currentActiveSlot = -1;
    public int[] priorities = new int[6];

    public FilteredRoutingNodeTile(BlockEntityType<?> type, int size, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public ItemStack getFilterStack(Direction side) {
        int index = side.get3DDataValue();
        return getItem(index);
    }

    public int getCurrentActiveSlot() {
        if (currentActiveSlot == -1 && level != null) {
            currentActiveSlot = 0;
            for (Direction dir : Direction.values()) {
                BlockPos offsetPos = this.getCurrentBlockPos().relative(dir);
                BlockEntity tile = level.getBlockEntity(offsetPos);
                if (tile != null) {
                    IItemHandler handler = level.getCapability(
                            Capabilities.ItemHandler.BLOCK, offsetPos, dir.getOpposite());
                    if (handler != null) {
                        currentActiveSlot = dir.ordinal();
                        break;
                    }
                }
            }
        }
        return Math.max(0, currentActiveSlot);
    }

    public void setCurrentActiveSlot(int slot) {
        this.currentActiveSlot = slot;
    }

    @Override
    public boolean isInventoryConnectedToSide(Direction side) {
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("currentSlot", currentActiveSlot);
        tag.putIntArray(Constants.NBT.ROUTING_PRIORITY, priorities);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        currentActiveSlot = tag.getInt("currentSlot");
        priorities = tag.getIntArray(Constants.NBT.ROUTING_PRIORITY);
        if (priorities.length != 6) {
            priorities = new int[6];
        }
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    public void swapFilters(int requestedSlot) {
        currentActiveSlot = requestedSlot;
        this.setChanged();
    }

    @Override
    public int getPriority(Direction side) {
        return priorities[side.get3DDataValue()];
    }

    public void incrementCurrentPriorityToMaximum(int max) {
        int slot = Math.max(0, currentActiveSlot);
        priorities[slot] = Math.min(priorities[slot] + 1, max);
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
        setChanged();
    }

    public void decrementCurrentPriority() {
        int slot = Math.max(0, currentActiveSlot);
        priorities[slot] = Math.max(priorities[slot] - 1, 0);
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
        setChanged();
    }

    /**
     * Swaps the priority of the current direction with the specified direction.
     * @param otherSlot The direction slot (0-5) to swap priority with
     */
    public void swapPriorityWith(int otherSlot) {
        if (otherSlot < 0 || otherSlot >= 6) return;
        int currentSlot = Math.max(0, currentActiveSlot);
        if (currentSlot == otherSlot) return;

        int temp = priorities[currentSlot];
        priorities[currentSlot] = priorities[otherSlot];
        priorities[otherSlot] = temp;

        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
        setChanged();
    }

    /**
     * Gets the display name of the neighbor block in the specified direction.
     * @param dir The direction to check
     * @return The block name, or "None" if empty/no block
     */
    public String getNeighborName(Direction dir) {
        if (level == null) return "None";
        BlockPos neighborPos = worldPosition.relative(dir);
        BlockState state = level.getBlockState(neighborPos);
        if (state.isAir()) return "None";

        // Check if it's an inventory
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, dir.getOpposite());
        if (handler != null) {
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be != null) {
                return state.getBlock().getName().getString();
            }
        }
        return state.getBlock().getName().getString();
    }

    /**
     * Checks if there's an inventory neighbor in the specified direction.
     * @param dir The direction to check
     * @return true if there's an inventory
     */
    public boolean hasInventoryNeighbor(Direction dir) {
        if (level == null) return false;
        BlockPos neighborPos = worldPosition.relative(dir);
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, dir.getOpposite());
        return handler != null;
    }

    // Container implementation
    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    // WorldlyContainer implementation
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }
}
