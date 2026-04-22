package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.routing.*;
import com.breakinblocks.neovitae.util.Utils;

/**
 * Output routing node - pushes items to connected inventories.
 */
public class OutputRoutingNodeBlockEntity extends FilteredRoutingNodeBlockEntity implements IOutputItemRoutingNode, IOutputFluidRoutingNode, MenuProvider {

    public OutputRoutingNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public OutputRoutingNodeBlockEntity(BlockPos pos, BlockState state) {
        this(NVTiles.OUTPUT_ROUTING_NODE_TYPE.get(), pos, state);
    }

    @Override
    public boolean isOutput(Direction side) {
        return getSideFilter(side).isEnabled();
    }

    @Override
    public IItemFilter getOutputFilterForSide(Direction side) {
        SideFilterConfig cfg = getSideFilter(side);
        if (!cfg.isEnabled()) return null;

        BlockEntity tile = getLevel().getBlockEntity(worldPosition.relative(side));
        if (tile == null) return null;

        IItemHandler handler = Utils.getInventory(tile, side.getOpposite());
        if (handler == null) return null;

        return RoutingFilterFactory.createItemFilter(cfg, tile, handler, true);
    }

    @Override
    public boolean isTankConnectedToSide(Direction side) {
        return true;
    }

    @Override
    public int getFluidPriority(Direction side) {
        return priorities[side.get3DDataValue()];
    }

    @Override
    public boolean isFluidOutput(Direction side) {
        return getSideFilter(side).isEnabled();
    }

    @Override
    public IFluidFilter getOutputFluidFilterForSide(Direction side) {
        SideFilterConfig cfg = getSideFilter(side);
        if (!cfg.isEnabled()) return null;

        BlockPos neighborPos = worldPosition.relative(side);
        var rhFluid = getLevel().getCapability(Capabilities.Fluid.BLOCK, neighborPos, side.getOpposite());
        IFluidHandler handler = rhFluid != null ? IFluidHandler.of(rhFluid) : null;
        if (handler == null) return null;

        BlockEntity tile = getLevel().getBlockEntity(neighborPos);
        return RoutingFilterFactory.createFluidFilter(cfg, tile, handler, true);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RoutingNodeMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.output_routing_node");
    }
}
