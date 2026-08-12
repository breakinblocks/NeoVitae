package com.breakinblocks.neovitae.common.blockentity.routing;

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
import java.util.Iterator;
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
    private int connectionSweepTimer = 0;

    private static final int CONNECTION_SWEEP_INTERVAL = 100;

    public RoutingNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        currentInput = level.getBestNeighborSignal(pos);

        if (bindingNeedsValidation) {
            validateBinding(level);
        }

        if (++connectionSweepTimer >= CONNECTION_SWEEP_INTERVAL) {
            connectionSweepTimer = 0;
            pruneDeadConnections(level);
        }
    }

    /**
     * Drops connections whose far end is no longer a routing node. A node destroyed while a
     * neighbour was unloaded never gets cleaned up by {@link #removeAllConnections()}, and the
     * orphaned entry would otherwise keep drawing a beam to an empty position forever.
     * Positions in unloaded chunks are left alone until they can actually be checked.
     */
    private void pruneDeadConnections(Level level) {
        if (connectionList.isEmpty()) return;

        boolean changed = false;
        Iterator<BlockPos> iterator = connectionList.iterator();
        while (iterator.hasNext()) {
            BlockPos connected = iterator.next();
            if (!level.hasChunk(connected.getX() >> 4, connected.getZ() >> 4)) continue;
            if (level.getBlockEntity(connected) instanceof IRoutingNode) continue;
            iterator.remove();
            changed = true;
        }

        if (!changed) return;

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        if (level.getBlockEntity(masterPos) instanceof IMasterRoutingNode master) {
            master.removeNodeFromGraph(worldPosition);
            master.addConnections(worldPosition, connectionList);
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        // Position stamp: lets loadAdditional detect if a mover copied our BE to a new spot.
        tag.putLong("savedAt", worldPosition.asLong());

        CompoundTag masterTag = new CompoundTag();
        masterTag.putInt(Constants.NBT.X_COORD, masterPos.getX());
        masterTag.putInt(Constants.NBT.Y_COORD, masterPos.getY());
        masterTag.putInt(Constants.NBT.Z_COORD, masterPos.getZ());
        tag.put(Constants.NBT.ROUTING_MASTER, masterTag);

        ListTag tags = new ListTag();
        for (BlockPos connPos : connectionList) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt(Constants.NBT.X_COORD, connPos.getX());
            posTag.putInt(Constants.NBT.Y_COORD, connPos.getY());
            posTag.putInt(Constants.NBT.Z_COORD, connPos.getZ());
            tags.add(posTag);
        }
        tag.put(Constants.NBT.ROUTING_CONNECTION, tags);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        connectionList.clear();

        CompoundTag masterTag = tag.getCompound(Constants.NBT.ROUTING_MASTER);
        masterPos = new BlockPos(
                masterTag.getInt(Constants.NBT.X_COORD),
                masterTag.getInt(Constants.NBT.Y_COORD),
                masterTag.getInt(Constants.NBT.Z_COORD));

        ListTag tags = tag.getList(Constants.NBT.ROUTING_CONNECTION, 10);
        for (int i = 0; i < tags.size(); i++) {
            CompoundTag blockTag = tags.getCompound(i);
            BlockPos newPos = new BlockPos(
                    blockTag.getInt(Constants.NBT.X_COORD),
                    blockTag.getInt(Constants.NBT.Y_COORD),
                    blockTag.getInt(Constants.NBT.Z_COORD));
            connectionList.add(newPos);
        }

        // Detect a block-mover that preserved the BE but changed its position.
        if (tag.contains("savedAt", Tag.TAG_LONG)) {
            BlockPos savedAt = BlockPos.of(tag.getLong("savedAt"));
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
        if (level != null && level.isClientSide) {
            RoutingBeamHandler.register(this);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && level.isClientSide) {
            RoutingBeamHandler.unregister(this);
        }
        super.setRemoved();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void removeAllConnections() {
        BlockEntity testTile = getLevel().getBlockEntity(getMasterPos());
        if (testTile instanceof IMasterRoutingNode master) {
            master.removeNodeFromGraph(worldPosition);
            master.removeConnection(worldPosition);
        }

        // Opportunistic cleanup of loaded neighbors; the master's graph is authoritative for unloaded ones.
        for (BlockPos testPos : connectionList) {
            BlockEntity tile = getLevel().getBlockEntity(testPos);
            if (tile instanceof IRoutingNode node) {
                node.removeConnection(worldPosition);
                getLevel().sendBlockUpdated(testPos, getLevel().getBlockState(testPos), getLevel().getBlockState(testPos), 3);
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
            connectionList.add(pos);
            setChanged();
            getLevel().sendBlockUpdated(getBlockPos(), getLevel().getBlockState(getBlockPos()),
                    getLevel().getBlockState(getBlockPos()), 3);
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
