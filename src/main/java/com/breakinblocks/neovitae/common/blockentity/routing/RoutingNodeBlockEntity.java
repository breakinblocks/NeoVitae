package com.breakinblocks.neovitae.common.blockentity.routing;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.client.event.RoutingBeamHandler;
import com.breakinblocks.neovitae.common.routing.RoutingLinkHelper;
import com.breakinblocks.neovitae.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base tile entity for routing nodes. Concrete subclasses are
 * {@link RoutingConduitBlockEntity} (the plain graph-edge relay) and the
 * filtered variants (input/output/master) under {@link FilteredRoutingNodeBlockEntity}.
 */
public abstract class RoutingNodeBlockEntity extends BlockEntity implements IRoutingNode, IItemRoutingNode {

    private int currentInput;
    private BlockPos masterPos = BlockPos.ZERO;
    private List<BlockPos> connectionList = new ArrayList<>();

    private boolean bindingNeedsValidation = true;

    public RoutingNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        currentInput = level.getBestNeighborSignal(pos);

        if (bindingNeedsValidation) {
            validateBinding(level);
        }
    }

    /**
     * Runs once per (re)load: verify the master still knows about us, else reset and
     * try to auto-bind. Deferred if the master's chunk isn't loaded yet.
     */
    private void validateBinding(Level level) {
        if (!masterPos.equals(BlockPos.ZERO)) {
            int mcx = masterPos.getX() >> 4;
            int mcz = masterPos.getZ() >> 4;
            if (!level.hasChunk(mcx, mcz)) return;

            BlockEntity masterTile = level.getBlockEntity(masterPos);
            boolean valid = masterTile instanceof MasterRoutingNodeBlockEntity master
                    && master.graphContains(worldPosition);
            if (!valid) {
                masterPos = BlockPos.ZERO;
                connectionList.clear();
                setChanged();
            }
        }

        if (masterPos.equals(BlockPos.ZERO)) {
            RoutingLinkHelper.tryAutoBind(level, worldPosition, this);
        }

        bindingNeedsValidation = false;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);

        tag.putLong("savedAt", worldPosition.asLong());

        tag.store(Constants.NBT.ROUTING_MASTER, BlockPos.CODEC, masterPos);

        tag.store(Constants.NBT.ROUTING_CONNECTION, BlockPos.CODEC.listOf(), List.copyOf(connectionList));
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        connectionList.clear();

        masterPos = tag.read(Constants.NBT.ROUTING_MASTER, BlockPos.CODEC).orElse(BlockPos.ZERO);

        tag.read(Constants.NBT.ROUTING_CONNECTION, BlockPos.CODEC.listOf())
                .ifPresent(connectionList::addAll);

        // Detect a block-mover that preserved the BE but changed its position.
        long savedAtLong = tag.getLongOr("savedAt", Long.MIN_VALUE);
        if (savedAtLong != Long.MIN_VALUE) {
            BlockPos savedAt = BlockPos.of(savedAtLong);
            if (!savedAt.equals(worldPosition)) {
                masterPos = BlockPos.ZERO;
                connectionList.clear();
            }
        }

        bindingNeedsValidation = true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        bindingNeedsValidation = true;
        if (level != null && level.isClientSide()) {
            RoutingBeamHandler.register(this);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && level.isClientSide()) {
            RoutingBeamHandler.unregister(this);
        }
        super.setRemoved();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void removeAllConnections() {
        Level level = getLevel();
        if (level == null) {
            connectionList.clear();
            return;
        }

        BlockEntity testTile = level.getBlockEntity(getMasterPos());
        if (testTile instanceof IMasterRoutingNode master) {
            master.removeNodeFromGraph(worldPosition);
            master.removeConnection(worldPosition);
        }

        // Opportunistic cleanup of loaded neighbors; the master's graph is authoritative for unloaded ones.
        for (BlockPos testPos : connectionList) {
            BlockEntity tile = level.getBlockEntity(testPos);
            if (tile instanceof IRoutingNode node) {
                node.removeConnection(worldPosition);
                level.sendBlockUpdated(testPos, level.getBlockState(testPos), level.getBlockState(testPos), 3);
            }
        }

        connectionList.clear();
    }

    @Override
    public void connectMasterToRemainingNode(Level level, List<BlockPos> alreadyChecked, IMasterRoutingNode master) {
        this.masterPos = master.getCurrentBlockPos();
        List<BlockPos> connectedList = this.getConnected();

        for (BlockPos testPos : connectedList) {
            if (alreadyChecked.contains(testPos)) {
                continue;
            }
            alreadyChecked.add(testPos);

            BlockEntity tile = level.getBlockEntity(testPos);
            if (!(tile instanceof IRoutingNode node)) {
                continue;
            }

            if (node.getMasterPos().equals(BlockPos.ZERO)) {
                master.addNodeToList(node);
                node.connectMasterToRemainingNode(level, alreadyChecked, master);
            }
        }

        master.addConnections(this.getCurrentBlockPos(), connectedList);
    }

    @Override
    public Triple<Boolean, List<BlockPos>, List<IRoutingNode>> recheckConnectionToMaster(
            List<BlockPos> alreadyChecked, List<IRoutingNode> nodeList) {

        if (this.masterPos.equals(BlockPos.ZERO)) {
            return Triple.of(false, alreadyChecked, nodeList);
        }

        List<BlockPos> connectedList = this.getConnected();
        for (BlockPos testPos : connectedList) {
            if (alreadyChecked.contains(testPos)) {
                continue;
            }
            alreadyChecked.add(testPos);

            BlockEntity tile = level.getBlockEntity(testPos);
            if (!(tile instanceof IRoutingNode node)) {
                continue;
            }

            if (node instanceof IMasterRoutingNode) {
                return Triple.of(true, alreadyChecked, nodeList);
            }

            Triple<Boolean, List<BlockPos>, List<IRoutingNode>> checkResult =
                    node.recheckConnectionToMaster(alreadyChecked, nodeList);

            if (checkResult.getLeft()) {
                return checkResult;
            }
        }

        nodeList.add(this);
        return Triple.of(false, alreadyChecked, nodeList);
    }

    @Override
    public List<BlockPos> checkAndPurgeConnectionToMaster(BlockPos ignorePos) {
        List<BlockPos> posList = new ArrayList<>();
        posList.add(ignorePos);

        Triple<Boolean, List<BlockPos>, List<IRoutingNode>> recheckResult =
                recheckConnectionToMaster(posList, new ArrayList<>());

        if (!recheckResult.getLeft()) {
            BlockEntity testTile = level.getBlockEntity(masterPos);
            IMasterRoutingNode masterNode = null;

            if (testTile instanceof IMasterRoutingNode) {
                masterNode = (IMasterRoutingNode) testTile;
                masterNode.removeConnection(getCurrentBlockPos(), getCurrentBlockPos());
            }

            for (IRoutingNode node : recheckResult.getRight()) {
                BlockPos nodeMasterPos = node.getMasterPos();
                node.removeConnection(nodeMasterPos);
                if (masterNode != null) {
                    masterNode.removeConnection(node.getCurrentBlockPos(), node.getCurrentBlockPos());
                }
            }

            return recheckResult.getMiddle();
        }

        return recheckResult.getMiddle();
    }

    @Override
    public BlockPos getCurrentBlockPos() {
        return this.getBlockPos();
    }

    @Override
    public List<BlockPos> getConnected() {
        return connectionList;
    }

    @Override
    public BlockPos getMasterPos() {
        return masterPos;
    }

    @Override
    public boolean isMaster(IMasterRoutingNode master) {
        BlockPos checkPos = master.getCurrentBlockPos();
        return checkPos.equals(getMasterPos());
    }

    @Override
    public boolean isConnectionEnabled(BlockPos testPos) {
        return currentInput <= 0;
    }

    @Override
    public void addConnection(BlockPos pos) {
        if (!connectionList.contains(pos)) {
            getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()),
                    getLevel().getBlockState(getBlockPos()), 3);
            connectionList.add(pos);
            setChanged();
        }
    }

    @Override
    public void removeConnection(BlockPos pos) {
        if (connectionList.contains(pos)) {
            connectionList.remove(pos);
            getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()),
                    getLevel().getBlockState(getBlockPos()), 3);
            setChanged();
        }

        if (pos.equals(masterPos)) {
            this.masterPos = BlockPos.ZERO;
            setChanged();
        }
    }

    @Override
    public boolean isInventoryConnectedToSide(Direction side) {
        return false;
    }

    @Override
    public int getPriority(Direction side) {
        return 0;
    }

}
