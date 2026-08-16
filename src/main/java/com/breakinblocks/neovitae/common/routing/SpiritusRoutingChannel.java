package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.api.capability.NVCapabilities;
import com.breakinblocks.neovitae.api.routing.ISpiritusFilter;
import com.breakinblocks.neovitae.api.routing.ISpiritusExportNode;
import com.breakinblocks.neovitae.api.routing.RoutingChannel;
import com.breakinblocks.neovitae.api.spiritus.ISpiritusStorage;
import com.breakinblocks.neovitae.common.blockentity.routing.FilteredRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import javax.annotation.Nullable;

public class SpiritusRoutingChannel implements RoutingChannel<ISpiritusFilter> {

    @Override
    public String id() {
        return "spiritus";
    }

    @Nullable
    private static ISpiritusStorage getStorage(BlockEntity be) {
        if (be.getLevel() == null) return null;
        return be.getLevel().getCapability(NVCapabilities.SPIRITUS_STORAGE, be.getBlockPos(), null);
    }

    @Override
    public boolean isInputNode(BlockEntity be) {
        return getStorage(be) != null;
    }

    @Override
    public boolean isOutputNode(BlockEntity be) {
        return be instanceof ISpiritusExportNode;
    }

    @Override
    public boolean isConnectedOnSide(BlockEntity be, Direction side) {
        return side == Direction.UP;
    }

    @Override
    public boolean isInputSide(BlockEntity be, Direction side) {
        return isInputNode(be);
    }

    @Override
    public boolean isOutputSide(BlockEntity be, Direction side) {
        return be instanceof ISpiritusExportNode node
                && node.getSpiritusExportType() != null
                && node.getSpiritusStockTarget() > 0;
    }

    @Override
    public int getPriority(BlockEntity be, Direction side) {
        return be instanceof FilteredRoutingNodeBlockEntity node ? node.priorities[side.get3DDataValue()] : 0;
    }

    @Override
    @Nullable
    public ISpiritusFilter getInputFilter(BlockEntity be, Direction side) {
        ISpiritusStorage storage = getStorage(be);
        if (storage == null || storage.getStoredType() == null || storage.getStored() <= 0) return null;
        return new AccumulatorSpiritusFilter(be, storage);
    }

    @Override
    @Nullable
    public ISpiritusFilter getOutputFilter(BlockEntity be, Direction side) {
        if (!(be instanceof ISpiritusExportNode node)) return null;
        SpiritusType type = node.getSpiritusExportType();
        if (type == null || node.getSpiritusStockTarget() <= 0) return null;
        return new ChunkStockSpiritusFilter(be, type, node.getSpiritusStockTarget());
    }

    @Override
    public int getMaxTransfer(BlockEntity masterNode) {
        return ((MasterRoutingNodeBlockEntity) masterNode).getMaxTransfer();
    }

    @Override
    public int transfer(ISpiritusFilter inputFilter, ISpiritusFilter outputFilter, int maxTransfer) {
        return inputFilter.transferThroughInputFilter(outputFilter, maxTransfer);
    }
}
