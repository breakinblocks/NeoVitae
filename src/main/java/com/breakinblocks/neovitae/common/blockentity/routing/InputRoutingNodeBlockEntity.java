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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.routing.*;
import com.breakinblocks.neovitae.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Input routing node - pulls items from connected inventories.
 */
public class InputRoutingNodeBlockEntity extends FilteredRoutingNodeBlockEntity implements IInputItemRoutingNode, IInputFluidRoutingNode, MenuProvider {

    public InputRoutingNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, 6, pos, state);
    }

    public InputRoutingNodeBlockEntity(BlockPos pos, BlockState state) {
        this(NVTiles.INPUT_ROUTING_NODE_TYPE.get(), pos, state);
    }

    @Override
    public boolean isInput(Direction side) {
        return true;
    }

    @Override
    public IItemFilter getInputFilterForSide(Direction side) {
        BlockEntity tile = getLevel().getBlockEntity(worldPosition.relative(side));
        if (tile != null) {
            IItemHandler handler = Utils.getInventory(tile, side.getOpposite());
            if (handler != null) {
                ItemStack filterStack = this.getFilterStack(side);

                if (filterStack.isEmpty() || !(filterStack.getItem() instanceof IItemFilterProvider filter)) {
                    return null;
                }

                return filter.getInputItemFilter(filterStack, tile, handler);
            }
        }
        return null;
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
    public boolean isFluidInput(Direction side) {
        return true;
    }

    @Override
    public IFluidFilter getInputFluidFilterForSide(Direction side) {
        BlockPos neighborPos = worldPosition.relative(side);
        IFluidHandler handler = getLevel().getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, side.getOpposite());
        if (handler == null) return null;

        ItemStack filterStack = this.getFilterStack(side);
        if (filterStack.isEmpty() || !(filterStack.getItem() instanceof IRoutingFilterProvider)) return null;

        BlockEntity tile = getLevel().getBlockEntity(neighborPos);
        List<FluidStack> passAll = new ArrayList<>();
        for (int tank = 0; tank < handler.getTanks(); tank++) {
            FluidStack fluid = handler.getFluidInTank(tank);
            if (!fluid.isEmpty()) {
                FluidStack copy = fluid.copy();
                copy.setAmount(handler.getTankCapacity(tank));
                passAll.add(copy);
            }
        }

        if (passAll.isEmpty()) return null;

        BasicFluidFilter filter = new BasicFluidFilter();
        filter.initializeFilter(passAll, tile, handler, false);
        return filter;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RoutingNodeMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.input_routing_node");
    }
}
