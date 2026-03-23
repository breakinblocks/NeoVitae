package com.breakinblocks.neovitae.common.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;

import javax.annotation.Nullable;

public class FluidRoutingChannel implements RoutingChannel<IFluidFilter> {

    @Override
    public String id() {
        return "fluid";
    }

    @Override
    public boolean isInputNode(BlockEntity be) {
        return be instanceof IInputFluidRoutingNode;
    }

    @Override
    public boolean isOutputNode(BlockEntity be) {
        return be instanceof IOutputFluidRoutingNode;
    }

    @Override
    public boolean isConnectedOnSide(BlockEntity be, Direction side) {
        return be instanceof IFluidRoutingNode node && node.isTankConnectedToSide(side);
    }

    @Override
    public boolean isInputSide(BlockEntity be, Direction side) {
        return be instanceof IInputFluidRoutingNode node && node.isFluidInput(side);
    }

    @Override
    public boolean isOutputSide(BlockEntity be, Direction side) {
        return be instanceof IOutputFluidRoutingNode node && node.isFluidOutput(side);
    }

    @Override
    public int getPriority(BlockEntity be, Direction side) {
        return be instanceof IFluidRoutingNode node ? node.getFluidPriority(side) : 0;
    }

    @Override
    @Nullable
    public IFluidFilter getInputFilter(BlockEntity be, Direction side) {
        return be instanceof IInputFluidRoutingNode node ? node.getInputFluidFilterForSide(side) : null;
    }

    @Override
    @Nullable
    public IFluidFilter getOutputFilter(BlockEntity be, Direction side) {
        return be instanceof IOutputFluidRoutingNode node ? node.getOutputFluidFilterForSide(side) : null;
    }

    @Override
    public int getMaxTransfer(BlockEntity masterNode) {
        return ((MasterRoutingNodeBlockEntity) masterNode).getMaxFluidTransfer();
    }

    @Override
    public int transfer(IFluidFilter inputFilter, IFluidFilter outputFilter, int maxTransfer) {
        return inputFilter.transferThroughInputFilter(outputFilter, maxTransfer);
    }
}
