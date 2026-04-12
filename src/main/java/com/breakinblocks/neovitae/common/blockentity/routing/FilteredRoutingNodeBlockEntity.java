package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.routing.FilterMode;
import com.breakinblocks.neovitae.common.routing.SideFilterConfig;
import com.breakinblocks.neovitae.util.Constants;

/** Routing node with per-side {@link SideFilterConfig} stored directly on the BE. */
public class FilteredRoutingNodeBlockEntity extends RoutingNodeBlockEntity {

    protected final SideFilterConfig[] sideFilters = new SideFilterConfig[6];
    private int currentActiveSlot = -1;
    public int[] priorities = new int[6];

    public FilteredRoutingNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (int i = 0; i < 6; i++) {
            sideFilters[i] = new SideFilterConfig();
        }
    }

    public SideFilterConfig getSideFilter(Direction side) {
        return sideFilters[side.get3DDataValue()];
    }

    public SideFilterConfig getSideFilter(int index) {
        return sideFilters[index];
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
                        currentActiveSlot = dir.get3DDataValue();
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

        ListTag sides = new ListTag();
        for (int i = 0; i < 6; i++) {
            sides.add(sideFilters[i].save(registries));
        }
        tag.put("SideFilters", sides);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        currentActiveSlot = tag.getInt("currentSlot");
        priorities = tag.getIntArray(Constants.NBT.ROUTING_PRIORITY);
        if (priorities.length != 6) {
            priorities = new int[6];
        }

        if (tag.contains("SideFilters", Tag.TAG_LIST)) {
            ListTag sides = tag.getList("SideFilters", Tag.TAG_COMPOUND);
            for (int i = 0; i < Math.min(6, sides.size()); i++) {
                sideFilters[i].load(sides.getCompound(i), registries);
            }
        }
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
        markUpdated();
    }

    public void decrementCurrentPriority() {
        int slot = Math.max(0, currentActiveSlot);
        priorities[slot] = Math.max(priorities[slot] - 1, 0);
        markUpdated();
    }

    public void swapPriorityWith(int otherSlot) {
        if (otherSlot < 0 || otherSlot >= 6) return;
        int currentSlot = Math.max(0, currentActiveSlot);
        if (currentSlot == otherSlot) return;

        int temp = priorities[currentSlot];
        priorities[currentSlot] = priorities[otherSlot];
        priorities[otherSlot] = temp;
        markUpdated();
    }

    public void setSideEnabled(int sideIndex, boolean enabled) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setEnabled(enabled);
        markUpdated();
    }

    public void toggleSideItemMode(int sideIndex) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        SideFilterConfig cfg = sideFilters[sideIndex];
        cfg.setItemMode(cfg.getItemMode().toggle());
        markUpdated();
    }

    public void toggleSideFluidMode(int sideIndex) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        SideFilterConfig cfg = sideFilters[sideIndex];
        cfg.setFluidMode(cfg.getFluidMode().nextFluidMode());
        markUpdated();
    }

    public void setItemGhost(int sideIndex, int ghostSlot, ItemStack stack) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        ItemStack copy = stack.copy();
        if (!copy.isEmpty()) {
            copy.setCount(1);
        }
        sideFilters[sideIndex].setItemGhost(ghostSlot, copy);
        markUpdated();
    }

    public void clearItemGhost(int sideIndex, int ghostSlot) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setItemGhost(ghostSlot, ItemStack.EMPTY);
        markUpdated();
    }

    public void setFluidGhost(int sideIndex, int ghostSlot, FluidStack stack) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        FluidStack copy = stack == null ? FluidStack.EMPTY : stack.copy();
        if (!copy.isEmpty()) {
            copy.setAmount(1);
        }
        sideFilters[sideIndex].setFluidGhost(ghostSlot, copy);
        markUpdated();
    }

    public void clearFluidGhost(int sideIndex, int ghostSlot) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setFluidGhost(ghostSlot, FluidStack.EMPTY);
        markUpdated();
    }

    private void markUpdated() {
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
        setChanged();
    }

    public String getNeighborName(Direction dir) {
        if (level == null) return "None";
        BlockPos neighborPos = worldPosition.relative(dir);
        BlockState state = level.getBlockState(neighborPos);
        if (state.isAir()) return "None";
        return state.getBlock().getName().getString();
    }

    public boolean hasInventoryNeighbor(Direction dir) {
        if (level == null) return false;
        BlockPos neighborPos = worldPosition.relative(dir);
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, dir.getOpposite());
        return handler != null;
    }
}
