package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.routing.IFilterKey;
import com.breakinblocks.neovitae.api.routing.IFluidFilter;
import com.breakinblocks.neovitae.api.routing.IItemFilter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.common.item.routing.BasicFilterKey;
import com.breakinblocks.neovitae.common.item.routing.ComponentFilterKey;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RoutingFilterFactory {

    private RoutingFilterFactory() {}

    public static IItemFilter createItemFilter(SideFilterConfig cfg, BlockEntity tile, ResourceHandler<ItemResource> handler, boolean isOutput) {
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
        for (int i = 0; i < cfg.getItemSlotCount(); i++) {
            ItemStack ghost = cfg.getItemGhost(i);
            if (ghost.isEmpty()) continue;
            ItemStack keyStack = ghost.copy();
            keyStack.setCount(1);
            int amount = cfg.getItemAmount(i);
            int count = (whitelist && amount > 0) ? amount : Integer.MAX_VALUE;

            Set<Identifier> compIds = cfg.getItemComponents(i);
            if (!compIds.isEmpty()) {
                Set<DataComponentType<?>> types = new LinkedHashSet<>();
                for (Identifier id : compIds) {
                    DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(id);
                    if (type != null) types.add(type);
                }
                keys.add(new ComponentFilterKey(keyStack, types, count));
            } else {
                keys.add(new BasicFilterKey(keyStack, count));
            }
        }
        return keys;
    }

    public static IFluidFilter createFluidFilter(SideFilterConfig cfg, BlockEntity tile, ResourceHandler<FluidResource> handler, boolean isOutput) {
        FilterMode mode = cfg.getFluidMode();

        if (mode == FilterMode.AUTO_MATCH) {
            return buildAutoMatchFilter(tile, handler, isOutput);
        }

        boolean whitelist = mode == FilterMode.WHITELIST;
        List<FluidStack> fluidKeys = new ArrayList<>();
        for (int i = 0; i < cfg.getFluidSlotCount(); i++) {
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

    private static IFluidFilter buildAutoMatchFilter(BlockEntity tile, ResourceHandler<FluidResource> handler, boolean isOutput) {
        List<FluidStack> passAll = new ArrayList<>();
        for (int tank = 0; tank < handler.size(); tank++) {
            FluidResource r = handler.getResource(tank);
            if (!r.isEmpty()) {
                FluidStack copy = r.toStack(1);
                copy.setAmount(Integer.MAX_VALUE);
                passAll.add(copy);
            }
        }

        if (passAll.isEmpty()) {
            if (!isOutput) return null;
            BasicFluidFilter filter = new BasicFluidFilter() {
                @Override
                public FluidStack transferFluidThroughOutputFilter(FluidStack inputFluid) {
                    int filled;
                    try (Transaction tx = Transaction.openRoot()) {
                        filled = fluidHandler.insert(FluidResource.of(inputFluid), inputFluid.getAmount(), tx);
                        tx.commit();
                    }
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
