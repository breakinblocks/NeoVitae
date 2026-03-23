package com.breakinblocks.neovitae.common.routing;
import com.breakinblocks.neovitae.api.routing.*;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;

import javax.annotation.Nullable;

public class ItemRoutingChannel implements RoutingChannel<IItemFilter> {

    @Override
    public String id() {
        return "item";
    }

    @Override
    public boolean isInputNode(BlockEntity be) {
        return be instanceof IInputItemRoutingNode;
    }

    @Override
    public boolean isOutputNode(BlockEntity be) {
        return be instanceof IOutputItemRoutingNode;
    }

    @Override
    public boolean isConnectedOnSide(BlockEntity be, Direction side) {
        return be instanceof IItemRoutingNode node && node.isInventoryConnectedToSide(side);
    }

    @Override
    public boolean isInputSide(BlockEntity be, Direction side) {
        return be instanceof IInputItemRoutingNode node && node.isInput(side);
    }

    @Override
    public boolean isOutputSide(BlockEntity be, Direction side) {
        return be instanceof IOutputItemRoutingNode node && node.isOutput(side);
    }

    @Override
    public int getPriority(BlockEntity be, Direction side) {
        return be instanceof IItemRoutingNode node ? node.getPriority(side) : 0;
    }

    @Override
    @Nullable
    public IItemFilter getInputFilter(BlockEntity be, Direction side) {
        return be instanceof IInputItemRoutingNode node ? node.getInputFilterForSide(side) : null;
    }

    @Override
    @Nullable
    public IItemFilter getOutputFilter(BlockEntity be, Direction side) {
        return be instanceof IOutputItemRoutingNode node ? node.getOutputFilterForSide(side) : null;
    }

    @Override
    public int getMaxTransfer(BlockEntity masterNode) {
        return ((MasterRoutingNodeBlockEntity) masterNode).getMaxTransfer();
    }

    @Override
    public int transfer(IItemFilter inputFilter, IItemFilter outputFilter, int maxTransfer) {
        return inputFilter.transferThroughInputFilter(outputFilter, maxTransfer);
    }
}
