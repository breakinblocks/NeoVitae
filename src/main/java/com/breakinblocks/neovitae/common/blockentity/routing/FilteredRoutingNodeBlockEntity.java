package com.breakinblocks.neovitae.common.blockentity.routing;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.Set;
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
import com.breakinblocks.neovitae.common.routing.FaceDirection;
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
                    Object handler = level.getCapability(
                            Capabilities.Item.BLOCK, offsetPos, dir.getOpposite());
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
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("currentSlot", currentActiveSlot);
        tag.putIntArray(Constants.NBT.ROUTING_PRIORITY, priorities);

        ValueOutput filtersTag = tag.child("sideFilters");
        for (Direction dir : Direction.values()) {
            sideFilters[dir.get3DDataValue()].save(filtersTag.child(dir.getSerializedName()));
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        currentActiveSlot = tag.getIntOr("currentSlot", 0);
        tag.getIntArray(Constants.NBT.ROUTING_PRIORITY).ifPresent(arr -> {
            if (arr.length == 6) priorities = arr;
        });
        if (priorities.length != 6) {
            priorities = new int[6];
        }

        tag.child("sideFilters").ifPresent(filtersTag -> {
            for (Direction dir : Direction.values()) {
                filtersTag.child(dir.getSerializedName()).ifPresent(sideFilters[dir.get3DDataValue()]::load);
            }
        });
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

    public void cycleSideDirection(int sideIndex, boolean backwards) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        SideFilterConfig cfg = sideFilters[sideIndex];
        FaceDirection[] values = FaceDirection.values();
        int step = backwards ? values.length - 1 : 1;
        cfg.setDirection(values[(cfg.getDirection().ordinal() + step) % values.length]);
        markUpdated();
    }

    public void setSideEnergyEnabled(int sideIndex, boolean enabled) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setEnergyEnabled(enabled);
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

    public void setItemAmount(int sideIndex, int ghostSlot, int amount) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setItemAmount(ghostSlot, amount);
        markUpdated();
    }

    public void setItemComponents(int sideIndex, int ghostSlot, Set<Identifier> components) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setItemComponents(ghostSlot, components);
        markUpdated();
    }

    public void setFluidAmount(int sideIndex, int ghostSlot, int amount) {
        if (sideIndex < 0 || sideIndex >= 6) return;
        sideFilters[sideIndex].setFluidAmount(ghostSlot, amount);
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
        return level.getCapability(Capabilities.Item.BLOCK, neighborPos, dir.getOpposite()) != null;
    }
}
