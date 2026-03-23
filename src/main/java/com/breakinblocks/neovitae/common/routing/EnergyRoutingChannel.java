package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.api.routing.*;

import javax.annotation.Nullable;

public class EnergyRoutingChannel implements RoutingChannel<IEnergyFilter> {

    @Override
    public String id() {
        return "energy";
    }

    @Override
    public boolean isInputNode(BlockEntity be) {
        return be instanceof InputRoutingNodeBlockEntity;
    }

    @Override
    public boolean isOutputNode(BlockEntity be) {
        return be instanceof OutputRoutingNodeBlockEntity;
    }

    @Override
    public boolean isConnectedOnSide(BlockEntity be, Direction side) {
        return true;
    }

    @Override
    public boolean isInputSide(BlockEntity be, Direction side) {
        return be instanceof InputRoutingNodeBlockEntity;
    }

    @Override
    public boolean isOutputSide(BlockEntity be, Direction side) {
        return be instanceof OutputRoutingNodeBlockEntity;
    }

    @Override
    public int getPriority(BlockEntity be, Direction side) {
        return be instanceof FilteredRoutingNodeBlockEntity node ? node.priorities[side.get3DDataValue()] : 0;
    }

    @Override
    @Nullable
    public IEnergyFilter getInputFilter(BlockEntity be, Direction side) {
        if (!(be instanceof FilteredRoutingNodeBlockEntity node)) return null;

        ItemStack filterStack = node.getFilterStack(side);
        if (filterStack.isEmpty() || !(filterStack.getItem() instanceof IRoutingFilterProvider)) return null;

        BlockPos neighborPos = be.getBlockPos().relative(side);
        IEnergyStorage storage = be.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, side.getOpposite());
        if (storage == null || !storage.canExtract()) return null;

        return new BasicEnergyFilter(be.getLevel().getBlockEntity(neighborPos), storage, false);
    }

    @Override
    @Nullable
    public IEnergyFilter getOutputFilter(BlockEntity be, Direction side) {
        if (!(be instanceof FilteredRoutingNodeBlockEntity node)) return null;

        ItemStack filterStack = node.getFilterStack(side);
        if (filterStack.isEmpty() || !(filterStack.getItem() instanceof IRoutingFilterProvider)) return null;

        BlockPos neighborPos = be.getBlockPos().relative(side);
        IEnergyStorage storage = be.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, side.getOpposite());
        if (storage == null || !storage.canReceive()) return null;

        return new BasicEnergyFilter(be.getLevel().getBlockEntity(neighborPos), storage, true);
    }

    @Override
    public int getMaxTransfer(BlockEntity masterNode) {
        return RoutingNodeHelper.getEffectiveEnergyTransfer(
                ((MasterRoutingNodeBlockEntity) masterNode).getBlockState().getBlock(),
                ((MasterRoutingNodeBlockEntity) masterNode).getItem(MasterRoutingNodeBlockEntity.SLOT_STACK_UPGRADE).getCount()
        );
    }

    @Override
    public int transfer(IEnergyFilter inputFilter, IEnergyFilter outputFilter, int maxTransfer) {
        return inputFilter.transferThroughInputFilter(outputFilter, maxTransfer);
    }
}
