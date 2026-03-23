package com.breakinblocks.neovitae.common.blockentity.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.routing.*;
import com.breakinblocks.neovitae.util.Constants;

import java.util.*;
import java.util.Map.Entry;

public class MasterRoutingNodeBlockEntity extends BlockEntity implements IMasterRoutingNode, Container, MenuProvider {

    private static final int TREE_OFFSET = 10;

    public static final int SLOT_STACK_UPGRADE = 0;
    public static final int SLOT_SPEED_UPGRADE = 1;

    private int currentInput;
    private TreeMap<BlockPos, List<BlockPos>> connectionMap = new TreeMap<>();
    private List<BlockPos> generalNodeList = new ArrayList<>();

    // Channel-keyed node lists: channelId -> list of node positions
    private final Map<String, List<BlockPos>> inputNodeLists = new LinkedHashMap<>();
    private final Map<String, List<BlockPos>> outputNodeLists = new LinkedHashMap<>();

    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public MasterRoutingNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MasterRoutingNodeBlockEntity(BlockPos pos, BlockState state) {
        this(NVTiles.MASTER_ROUTING_NODE_TYPE.get(), pos, state);
    }

    private List<BlockPos> getInputList(String channelId) {
        return inputNodeLists.computeIfAbsent(channelId, k -> new ArrayList<>());
    }

    private List<BlockPos> getOutputList(String channelId) {
        return outputNodeLists.computeIfAbsent(channelId, k -> new ArrayList<>());
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        currentInput = level.getDirectSignalTo(pos);

        int tickMod = RoutingNodeHelper.getEffectiveTickRate(
                getBlockState().getBlock(),
                getItem(SLOT_SPEED_UPGRADE).getCount()
        );
        if (level.getGameTime() % tickMod != 0) return;

        Set<BlockPos> visitedNodes = new HashSet<>();

        for (RoutingChannel<?> channel : RoutingChannelRegistry.getChannels()) {
            processChannel(channel, visitedNodes, level);
        }
    }

    @SuppressWarnings("unchecked")
    private <F extends IRoutingFilter> void processChannel(RoutingChannel<F> channel,
                                                            Set<BlockPos> visitedNodes, Level level) {
        List<BlockPos> outputNodes = getOutputList(channel.id());
        List<BlockPos> inputNodes = getInputList(channel.id());

        Map<Integer, List<F>> outputMap = new TreeMap<>();
        for (BlockPos outputPos : outputNodes) {
            visitedNodes.clear();
            BlockEntity tile = level.getBlockEntity(outputPos);
            if (tile != null && isConnectedOptimized(visitedNodes, outputPos)) {
                for (Direction facing : Direction.values()) {
                    if (!channel.isConnectedOnSide(tile, facing) || !channel.isOutputSide(tile, facing)) continue;

                    F filter = channel.getOutputFilter(tile, facing);
                    if (filter != null) {
                        int priority = channel.getPriority(tile, facing);
                        outputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>()).add(filter);
                    }
                }
            }
        }

        Map<Integer, List<F>> inputMap = new TreeMap<>();
        for (BlockPos inputPos : inputNodes) {
            visitedNodes.clear();
            BlockEntity tile = level.getBlockEntity(inputPos);
            if (tile != null && isConnectedOptimized(visitedNodes, inputPos)) {
                for (Direction facing : Direction.values()) {
                    if (!channel.isConnectedOnSide(tile, facing) || !channel.isInputSide(tile, facing)) continue;

                    F filter = channel.getInputFilter(tile, facing);
                    if (filter != null) {
                        int priority = channel.getPriority(tile, facing);
                        inputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>()).add(filter);
                    }
                }
            }
        }

        int maxTransfer = channel.getMaxTransfer(this);

        for (Entry<Integer, List<F>> outputEntry : outputMap.entrySet()) {
            for (F outputFilter : outputEntry.getValue()) {
                for (Entry<Integer, List<F>> inputEntry : inputMap.entrySet()) {
                    for (F inputFilter : inputEntry.getValue()) {
                        int transferred = channel.transfer(inputFilter, outputFilter, maxTransfer);
                        maxTransfer -= transferred;
                        if (maxTransfer <= 0) return;
                    }
                }
            }
        }
    }

    private boolean isConnectedOptimized(Set<BlockPos> visited, BlockPos nodePos) {
        if (getLevel() == null) return false;

        BlockEntity tile = getLevel().getBlockEntity(nodePos);
        if (!(tile instanceof IRoutingNode node)) return false;

        List<BlockPos> connectionList = node.getConnected();
        visited.add(nodePos);

        for (BlockPos testPos : connectionList) {
            if (visited.contains(testPos)) continue;

            if (testPos.equals(this.getBlockPos()) && node.isConnectionEnabled(testPos)) {
                return true;
            } else if (node.isConnectionEnabled(testPos)) {
                BlockEntity testTile = getLevel().getBlockEntity(testPos);
                if (testTile instanceof IRoutingNode testNode && testNode.isConnectionEnabled(nodePos)) {
                    if (isConnectedOptimized(visited, testPos)) return true;
                }
            }
        }
        return false;
    }

    public int getMaxTransfer() {
        return RoutingNodeHelper.getEffectiveItemTransfer(
                getBlockState().getBlock(),
                getItem(SLOT_STACK_UPGRADE).getCount()
        );
    }

    public int getMaxFluidTransfer() {
        return RoutingNodeHelper.getEffectiveFluidTransfer(
                getBlockState().getBlock(),
                getItem(SLOT_STACK_UPGRADE).getCount()
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        savePosList(tag, Constants.NBT.ROUTING_MASTER_GENERAL, generalNodeList);

        for (RoutingChannel<?> channel : RoutingChannelRegistry.getChannels()) {
            savePosList(tag, "channel_input_" + channel.id(), getInputList(channel.id()));
            savePosList(tag, "channel_output_" + channel.id(), getOutputList(channel.id()));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        generalNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_GENERAL);

        for (RoutingChannel<?> channel : RoutingChannelRegistry.getChannels()) {
            String inputKey = "channel_input_" + channel.id();
            String outputKey = "channel_output_" + channel.id();

            inputNodeLists.put(channel.id(), loadPosList(tag, inputKey));
            outputNodeLists.put(channel.id(), loadPosList(tag, outputKey));
        }

        // Backward compatibility: migrate old per-type keys
        migrateOldKey(tag, Constants.NBT.ROUTING_MASTER_INPUT, "item", true);
        migrateOldKey(tag, Constants.NBT.ROUTING_MASTER_OUTPUT, "item", false);
        migrateOldKey(tag, Constants.NBT.ROUTING_MASTER_FLUID_INPUT, "fluid", true);
        migrateOldKey(tag, Constants.NBT.ROUTING_MASTER_FLUID_OUTPUT, "fluid", false);
    }

    private void migrateOldKey(CompoundTag tag, String oldKey, String channelId, boolean isInput) {
        if (!tag.contains(oldKey)) return;
        List<BlockPos> oldList = loadPosList(tag, oldKey);
        if (oldList.isEmpty()) return;

        List<BlockPos> target = isInput ? getInputList(channelId) : getOutputList(channelId);
        for (BlockPos pos : oldList) {
            if (!target.contains(pos)) {
                target.add(pos);
            }
        }
    }

    private void savePosList(CompoundTag tag, String key, List<BlockPos> list) {
        ListTag tags = new ListTag();
        for (BlockPos pos : list) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt(Constants.NBT.X_COORD, pos.getX());
            posTag.putInt(Constants.NBT.Y_COORD, pos.getY());
            posTag.putInt(Constants.NBT.Z_COORD, pos.getZ());
            tags.add(posTag);
        }
        tag.put(key, tags);
    }

    private List<BlockPos> loadPosList(CompoundTag tag, String key) {
        List<BlockPos> list = new ArrayList<>();
        ListTag tags = tag.getList(key, 10);
        for (int i = 0; i < tags.size(); i++) {
            CompoundTag blockTag = tags.getCompound(i);
            list.add(new BlockPos(
                    blockTag.getInt(Constants.NBT.X_COORD),
                    blockTag.getInt(Constants.NBT.Y_COORD),
                    blockTag.getInt(Constants.NBT.Z_COORD)));
        }
        return list;
    }

    @Override
    public boolean isConnected(List<BlockPos> path, BlockPos nodePos) {
        BlockEntity tile = getLevel().getBlockEntity(nodePos);
        if (!(tile instanceof IRoutingNode node)) return false;

        List<BlockPos> connectionList = node.getConnected();
        path.add(nodePos);

        for (BlockPos testPos : connectionList) {
            if (path.contains(testPos)) continue;

            if (testPos.equals(this.getBlockPos()) && node.isConnectionEnabled(testPos)) {
                return true;
            } else if (node.isConnectionEnabled(testPos)) {
                BlockEntity testTile = getLevel().getBlockEntity(testPos);
                if (testTile instanceof IRoutingNode testNode && testNode.isConnectionEnabled(nodePos)) {
                    if (isConnected(path, testPos)) return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConnectionEnabled(BlockPos testPos) {
        return currentInput <= 0;
    }

    @Override
    public void addNodeToList(IRoutingNode node) {
        BlockPos newPos = node.getCurrentBlockPos();
        if (!generalNodeList.contains(newPos)) {
            generalNodeList.add(newPos);
        }

        BlockEntity be = (BlockEntity) node;
        for (RoutingChannel<?> channel : RoutingChannelRegistry.getChannels()) {
            if (channel.isInputNode(be)) {
                List<BlockPos> list = getInputList(channel.id());
                if (!list.contains(newPos)) list.add(newPos);
            }
            if (channel.isOutputNode(be)) {
                List<BlockPos> list = getOutputList(channel.id());
                if (!list.contains(newPos)) list.add(newPos);
            }
        }
        setChanged();
    }

    @Override
    public void addConnections(BlockPos pos, List<BlockPos> connectionList) {
        for (BlockPos testPos : connectionList) {
            addConnection(pos, testPos);
        }
    }

    @Override
    public void addConnection(BlockPos pos1, BlockPos pos2) {
        connectionMap.computeIfAbsent(pos1, k -> new ArrayList<>());
        if (!connectionMap.get(pos1).contains(pos2)) {
            connectionMap.get(pos1).add(pos2);
        }

        connectionMap.computeIfAbsent(pos2, k -> new ArrayList<>());
        if (!connectionMap.get(pos2).contains(pos1)) {
            connectionMap.get(pos2).add(pos1);
        }
    }

    @Override
    public void removeConnection(BlockPos pos1, BlockPos pos2) {
        if (connectionMap.containsKey(pos1)) {
            connectionMap.get(pos1).remove(pos2);
            if (connectionMap.get(pos1).isEmpty()) connectionMap.remove(pos1);
        }
        if (connectionMap.containsKey(pos2)) {
            connectionMap.get(pos2).remove(pos1);
            if (connectionMap.get(pos2).isEmpty()) connectionMap.remove(pos2);
        }
    }

    @Override
    public void connectMasterToRemainingNode(Level level, List<BlockPos> alreadyChecked, IMasterRoutingNode master) {
    }

    @Override
    public BlockPos getCurrentBlockPos() {
        return this.getBlockPos();
    }

    @Override
    public List<BlockPos> getConnected() {
        return new ArrayList<>();
    }

    @Override
    public BlockPos getMasterPos() {
        return this.getBlockPos();
    }

    @Override
    public boolean isMaster(IMasterRoutingNode master) {
        return false;
    }

    @Override
    public void addConnection(BlockPos pos) {
    }

    @Override
    public void removeConnection(BlockPos pos) {
        generalNodeList.remove(pos);
        for (List<BlockPos> list : inputNodeLists.values()) list.remove(pos);
        for (List<BlockPos> list : outputNodeLists.values()) list.remove(pos);
        setChanged();
    }

    @Override
    public void removeAllConnections() {
        for (BlockPos testPos : new ArrayList<>(generalNodeList)) {
            BlockEntity tile = getLevel().getBlockEntity(testPos);
            if (tile instanceof IRoutingNode node) {
                node.removeConnection(worldPosition);
                getLevel().sendBlockUpdated(testPos, getLevel().getBlockState(testPos),
                        getLevel().getBlockState(testPos), 3);
            }
        }
        generalNodeList.clear();
        inputNodeLists.clear();
        outputNodeLists.clear();
        connectionMap.clear();
        setChanged();
    }

    @Override
    public Triple<Boolean, List<BlockPos>, List<IRoutingNode>> recheckConnectionToMaster(
            List<BlockPos> alreadyChecked, List<IRoutingNode> nodeList) {
        return Triple.of(true, alreadyChecked, nodeList);
    }

    @Override
    public List<BlockPos> checkAndPurgeConnectionToMaster(BlockPos ignorePos) {
        return new ArrayList<>();
    }

    // Container implementation
    @Override public int getContainerSize() { return items.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override public ItemStack getItem(int slot) { return items.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); }

    public int getGeneralNodeCount() { return generalNodeList.size(); }
    public int getInputNodeCount() { return getInputList("item").size(); }
    public int getOutputNodeCount() { return getOutputList("item").size(); }
    public int getFluidInputNodeCount() { return getInputList("fluid").size(); }
    public int getFluidOutputNodeCount() { return getOutputList("fluid").size(); }
    public int getChannelInputNodeCount(String channelId) { return getInputList(channelId).size(); }
    public int getChannelOutputNodeCount(String channelId) { return getOutputList(channelId).size(); }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.master_routing_node");
    }
}
