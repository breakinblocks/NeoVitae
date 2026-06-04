package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.api.routing.IFilterKey;
import com.breakinblocks.neovitae.api.routing.IFluidFilter;
import com.breakinblocks.neovitae.api.routing.IItemFilter;
import com.breakinblocks.neovitae.common.item.routing.BasicFilterKey;

import java.util.ArrayList;
import java.util.List;

public final class RoutingFilterFactory {

    private RoutingFilterFactory() {}

    public static IItemFilter createItemFilter(SideFilterConfig cfg, BlockEntity tile, IItemHandler handler, boolean isOutput) {
        IItemFilter filter = cfg.getItemMode() == FilterMode.BLACKLIST
                ? new BlacklistItemFilter()
                : new BasicItemFilter();

        List<IFilterKey> keys = buildFilterKeys(cfg);
        filter.initializeFilter(keys, tile, handler, isOutput);
        return filter;
    }

    private static List<IFilterKey> buildFilterKeys(SideFilterConfig cfg) {
        boolean whitelist = cfg.getItemMode() == FilterMode.WHITELIST;
        List<IFilterKey> keys = new ArrayList<>();
        for (int i = 0; i < SideFilterConfig.GHOST_SLOTS; i++) {
            ItemStack ghost = cfg.getItemGhost(i);
            if (ghost.isEmpty()) continue;
            ItemStack keyStack = ghost.copy();
            keyStack.setCount(1);
            int amount = cfg.getItemAmount(i);
            int count = (whitelist && amount > 0) ? amount : Integer.MAX_VALUE;
            keys.add(new BasicFilterKey(keyStack, count));
        }
        return keys;
    }

    /** Returns null if no filter should apply (e.g. AUTO_MATCH with an empty input tank). */
    public static IFluidFilter createFluidFilter(SideFilterConfig cfg, BlockEntity tile, IFluidHandler handler, boolean isOutput) {
        FilterMode mode = cfg.getFluidMode();

        if (mode == FilterMode.AUTO_MATCH) {
            return buildAutoMatchFilter(tile, handler, isOutput);
        }

        boolean whitelist = mode == FilterMode.WHITELIST;
        List<FluidStack> fluidKeys = new ArrayList<>();
        for (int i = 0; i < SideFilterConfig.GHOST_SLOTS; i++) {
            FluidStack ghost = cfg.getFluidGhost(i);
            if (ghost.isEmpty()) continue;
            FluidStack copy = ghost.copy();
            int amount = cfg.getFluidAmount(i);
            copy.setAmount((whitelist && amount > 0) ? amount : Integer.MAX_VALUE);
            fluidKeys.add(copy);
        }

        if (mode == FilterMode.WHITELIST) {
            if (fluidKeys.isEmpty()) return null;
            BasicFluidFilter filter = new BasicFluidFilter();
            filter.initializeFilter(fluidKeys, tile, handler, isOutput);
            return filter;
        }

        BlacklistFluidFilter filter = new BlacklistFluidFilter();
        filter.initializeFilter(fluidKeys, tile, handler, isOutput);
        return filter;
    }

    private static IFluidFilter buildAutoMatchFilter(BlockEntity tile, IFluidHandler handler, boolean isOutput) {
        List<FluidStack> passAll = new ArrayList<>();
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            FluidStack fluid = handler.getFluidInTank(tank);
            if (!fluid.isEmpty()) {
                FluidStack copy = fluid.copy();
                copy.setAmount(Integer.MAX_VALUE);
                passAll.add(copy);
            }
        }

        if (passAll.isEmpty()) {
            if (!isOutput) return null;
            // Empty output tank: bypass whitelist matching and fill directly whatever comes in.
            BasicFluidFilter filter = new BasicFluidFilter() {
                @Override
                public FluidStack transferFluidThroughOutputFilter(FluidStack inputFluid) {
                    int filled = fluidHandler.fill(inputFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0 && accessedTile != null) {
                        Level level = accessedTile.getLevel();
                        BlockPos pos = accessedTile.getBlockPos();
                        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                    }
                    FluidStack remainder = inputFluid.copy();
                    remainder.shrink(filled);
                    return remainder.isEmpty() ? FluidStack.EMPTY : remainder;
                }
            };
            filter.initializeFilter(List.of(), tile, handler, true);
            return filter;
        }

        BasicFluidFilter filter = new BasicFluidFilter();
        filter.initializeFilter(passAll, tile, handler, isOutput);
        return filter;
    }
}
