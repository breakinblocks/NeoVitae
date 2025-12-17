package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import com.breakinblocks.neovitae.common.routing.IItemRoutingNode;
import com.breakinblocks.neovitae.common.routing.IMasterRoutingNode;
import com.breakinblocks.neovitae.common.routing.IRoutingNode;
import com.breakinblocks.neovitae.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Base tile entity for routing nodes.
 */
public class RoutingNodeTile extends BlockEntity implements IRoutingNode, IItemRoutingNode {

    private int currentInput;
    private BlockPos masterPos = BlockPos.ZERO;
    private List<BlockPos> connectionList = new ArrayList<>();

    public RoutingNodeTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public RoutingNodeTile(BlockPos pos, BlockState state) {
        this(com.breakinblocks.neovitae.common.blockentity.BMTiles.ROUTING_NODE_TYPE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            currentInput = level.getBestNeighborSignal(pos);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

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
    }

    @Override
    public void removeAllConnections() {
        BlockEntity testTile = getLevel().getBlockEntity(getMasterPos());
        if (testTile instanceof IMasterRoutingNode master) {
            master.removeConnection(worldPosition, worldPosition);
        }

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
