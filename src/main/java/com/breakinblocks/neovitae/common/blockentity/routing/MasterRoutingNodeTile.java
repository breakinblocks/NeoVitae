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
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.common.routing.*;
import com.breakinblocks.neovitae.util.Constants;

import java.util.*;
import java.util.Map.Entry;

/**
 * Master routing node - coordinates the entire routing network.
 *
 * <p>Transfer rates and tick speed are configurable via the routing_node_stats datamap.
 * This allows modpack developers to adjust performance and addon mods to create
 * custom master nodes with different capabilities.</p>
 */
public class MasterRoutingNodeTile extends BlockEntity implements IMasterRoutingNode, Container, MenuProvider {

    private static final int TREE_OFFSET = 10;

    public static final int SLOT_STACK_UPGRADE = 0;
    public static final int SLOT_SPEED_UPGRADE = 1;

    private int currentInput;
    private TreeMap<BlockPos, List<BlockPos>> connectionMap = new TreeMap<>();
    private List<BlockPos> generalNodeList = new ArrayList<>();
    private List<BlockPos> outputNodeList = new ArrayList<>();
    private List<BlockPos> inputNodeList = new ArrayList<>();

    // Fluid routing lists
    private List<BlockPos> fluidOutputNodeList = new ArrayList<>();
    private List<BlockPos> fluidInputNodeList = new ArrayList<>();

    protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public MasterRoutingNodeTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MasterRoutingNodeTile(BlockPos pos, BlockState state) {
        this(BMTiles.MASTER_ROUTING_NODE_TYPE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        // Early exit for client - all routing logic is server-side only
        if (level.isClientSide) {
            return;
        }

        currentInput = level.getDirectSignalTo(pos);

        // Calculate effective tick rate based on block's datamap stats and upgrades
        int tickMod = RoutingNodeHelper.getEffectiveTickRate(
                getBlockState().getBlock(),
                getItem(SLOT_SPEED_UPGRADE).getCount()
        );
        if (level.getGameTime() % tickMod != 0) {
            return;
        }

        // Reuse a single HashSet for connectivity checking (performance optimization)
        Set<BlockPos> visitedNodes = new HashSet<>();

        // Collect output filters by priority
        Map<Integer, List<IItemFilter>> outputMap = new TreeMap<>();

        for (BlockPos outputPos : outputNodeList) {
            visitedNodes.clear();
            BlockEntity outputTile = level.getBlockEntity(outputPos);
            if (this.isConnectedOptimized(visitedNodes, outputPos)) {
                if (outputTile instanceof IOutputItemRoutingNode outputNode) {
                    for (Direction facing : Direction.values()) {
                        if (!outputNode.isInventoryConnectedToSide(facing) || !outputNode.isOutput(facing)) {
                            continue;
                        }

                        IItemFilter filter = outputNode.getOutputFilterForSide(facing);
                        if (filter != null) {
                            int priority = outputNode.getPriority(facing);
                            outputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>()).add(filter);
                        }
                    }
                }
            }
        }

        // Collect input filters by priority
        Map<Integer, List<IItemFilter>> inputMap = new TreeMap<>();

        for (BlockPos inputPos : inputNodeList) {
            visitedNodes.clear();
            BlockEntity inputTile = level.getBlockEntity(inputPos);
            if (this.isConnectedOptimized(visitedNodes, inputPos)) {
                if (inputTile instanceof IInputItemRoutingNode inputNode) {
                    for (Direction facing : Direction.values()) {
                        if (!inputNode.isInventoryConnectedToSide(facing) || !inputNode.isInput(facing)) {
                            continue;
                        }

                        IItemFilter filter = inputNode.getInputFilterForSide(facing);
                        if (filter != null) {
                            int priority = inputNode.getPriority(facing);
                            inputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>()).add(filter);
                        }
                    }
                }
            }
        }

        // Transfer items from inputs to outputs
        int maxTransfer = getMaxTransfer();

        for (Entry<Integer, List<IItemFilter>> outputEntry : outputMap.entrySet()) {
            for (IItemFilter outputFilter : outputEntry.getValue()) {
                for (Entry<Integer, List<IItemFilter>> inputEntry : inputMap.entrySet()) {
                    for (IItemFilter inputFilter : inputEntry.getValue()) {
                        int transferred = inputFilter.transferThroughInputFilter(outputFilter, maxTransfer);
                        maxTransfer -= transferred;

                        if (maxTransfer <= 0) {
                            break;
                        }
                    }
                    if (maxTransfer <= 0) break;
                }
                if (maxTransfer <= 0) break;
            }
            if (maxTransfer <= 0) break;
        }

        // === Fluid Routing ===
        // Collect fluid output filters by priority
        Map<Integer, List<IFluidFilter>> fluidOutputMap = new TreeMap<>();

        for (BlockPos outputPos : fluidOutputNodeList) {
            visitedNodes.clear();
            BlockEntity outputTile = level.getBlockEntity(outputPos);
            if (this.isConnectedOptimized(visitedNodes, outputPos)) {
                if (outputTile instanceof IOutputFluidRoutingNode outputNode) {
                    for (Direction facing : Direction.values()) {
                        if (!outputNode.isTankConnectedToSide(facing) || !outputNode.isFluidOutput(facing)) {
                            continue;
                        }

                        IFluidFilter filter = outputNode.getOutputFluidFilterForSide(facing);
                        if (filter != null) {
                            int priority = outputNode.getFluidPriority(facing);
                            fluidOutputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>()).add(filter);
                        }
                    }
                }
            }
        }

        // Collect fluid input filters by priority
        Map<Integer, List<IFluidFilter>> fluidInputMap = new TreeMap<>();

        for (BlockPos inputPos : fluidInputNodeList) {
            visitedNodes.clear();
            BlockEntity inputTile = level.getBlockEntity(inputPos);
            if (this.isConnectedOptimized(visitedNodes, inputPos)) {
                if (inputTile instanceof IInputFluidRoutingNode inputNode) {
                    for (Direction facing : Direction.values()) {
                        if (!inputNode.isTankConnectedToSide(facing) || !inputNode.isFluidInput(facing)) {
                            continue;
                        }

                        IFluidFilter filter = inputNode.getInputFluidFilterForSide(facing);
                        if (filter != null) {
                            int priority = inputNode.getFluidPriority(facing);
                            fluidInputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>()).add(filter);
                        }
                    }
                }
            }
        }

        // Transfer fluids from inputs to outputs
        int maxFluidTransfer = getMaxFluidTransfer();

        for (Entry<Integer, List<IFluidFilter>> outputEntry : fluidOutputMap.entrySet()) {
            for (IFluidFilter outputFilter : outputEntry.getValue()) {
                for (Entry<Integer, List<IFluidFilter>> inputEntry : fluidInputMap.entrySet()) {
                    for (IFluidFilter inputFilter : inputEntry.getValue()) {
                        int transferred = inputFilter.transferThroughInputFilter(outputFilter, maxFluidTransfer);
                        maxFluidTransfer -= transferred;

                        if (maxFluidTransfer <= 0) {
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Optimized connectivity check using HashSet for O(1) visited lookups.
     */
    private boolean isConnectedOptimized(Set<BlockPos> visited, BlockPos nodePos) {
        if (getLevel() == null) return false;

        BlockEntity tile = getLevel().getBlockEntity(nodePos);
        if (!(tile instanceof IRoutingNode node)) {
            return false;
        }

        List<BlockPos> connectionList = node.getConnected();
        visited.add(nodePos);

        for (BlockPos testPos : connectionList) {
            if (visited.contains(testPos)) continue;

            if (testPos.equals(this.getBlockPos()) && node.isConnectionEnabled(testPos)) {
                return true;
            } else if (node.isConnectionEnabled(testPos)) {
                BlockEntity testTile = getLevel().getBlockEntity(testPos);
                if (testTile instanceof IRoutingNode testNode && testNode.isConnectionEnabled(nodePos)) {
                    if (isConnectedOptimized(visited, testPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the maximum item transfer per operation based on block stats and upgrades.
     * Configurable via the routing_node_stats datamap.
     */
    public int getMaxTransfer() {
        return RoutingNodeHelper.getEffectiveItemTransfer(
                getBlockState().getBlock(),
                getItem(SLOT_STACK_UPGRADE).getCount()
        );
    }

    /**
     * Gets the maximum fluid transfer per operation based on block stats and upgrades.
     * Configurable via the routing_node_stats datamap.
     */
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
        savePosList(tag, Constants.NBT.ROUTING_MASTER_INPUT, inputNodeList);
        savePosList(tag, Constants.NBT.ROUTING_MASTER_OUTPUT, outputNodeList);
        savePosList(tag, Constants.NBT.ROUTING_MASTER_FLUID_INPUT, fluidInputNodeList);
        savePosList(tag, Constants.NBT.ROUTING_MASTER_FLUID_OUTPUT, fluidOutputNodeList);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);

        generalNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_GENERAL);
        inputNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_INPUT);
        outputNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_OUTPUT);
        fluidInputNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_FLUID_INPUT);
        fluidOutputNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_FLUID_OUTPUT);
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
        if (!(tile instanceof IRoutingNode node)) {
            return false;
        }

        List<BlockPos> connectionList = node.getConnected();
        path.add(nodePos);

        for (BlockPos testPos : connectionList) {
            if (path.contains(testPos)) continue;

            if (testPos.equals(this.getBlockPos()) && node.isConnectionEnabled(testPos)) {
                return true;
            } else if (node.isConnectionEnabled(testPos)) {
                BlockEntity testTile = getLevel().getBlockEntity(testPos);
                if (testTile instanceof IRoutingNode testNode && testNode.isConnectionEnabled(nodePos)) {
                    if (isConnected(path, testPos)) {
                        return true;
                    }
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
        // Item routing
        if (node instanceof IInputItemRoutingNode && !inputNodeList.contains(newPos)) {
            inputNodeList.add(newPos);
        }
        if (node instanceof IOutputItemRoutingNode && !outputNodeList.contains(newPos)) {
            outputNodeList.add(newPos);
        }
        // Fluid routing
        if (node instanceof IInputFluidRoutingNode && !fluidInputNodeList.contains(newPos)) {
            fluidInputNodeList.add(newPos);
        }
        if (node instanceof IOutputFluidRoutingNode && !fluidOutputNodeList.contains(newPos)) {
            fluidOutputNodeList.add(newPos);
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
            if (connectionMap.get(pos1).isEmpty()) {
                connectionMap.remove(pos1);
            }
        }
        if (connectionMap.containsKey(pos2)) {
            connectionMap.get(pos2).remove(pos1);
            if (connectionMap.get(pos2).isEmpty()) {
                connectionMap.remove(pos2);
            }
        }
    }

    @Override
    public void connectMasterToRemainingNode(Level level, List<BlockPos> alreadyChecked, IMasterRoutingNode master) {
        // Master doesn't propagate
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
        // Empty - master uses two-arg version
    }

    @Override
    public void removeConnection(BlockPos pos) {
        generalNodeList.remove(pos);
        inputNodeList.remove(pos);
        outputNodeList.remove(pos);
        fluidInputNodeList.remove(pos);
        fluidOutputNodeList.remove(pos);
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
        inputNodeList.clear();
        outputNodeList.clear();
        fluidInputNodeList.clear();
        fluidOutputNodeList.clear();
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
    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    public int getGeneralNodeCount() {
        return generalNodeList.size();
    }

    public int getInputNodeCount() {
        return inputNodeList.size();
    }

    public int getOutputNodeCount() {
        return outputNodeList.size();
    }

    public int getFluidInputNodeCount() {
        return fluidInputNodeList.size();
    }

    public int getFluidOutputNodeCount() {
        return fluidOutputNodeList.size();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.master_routing_node");
    }
}
