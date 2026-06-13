package com.breakinblocks.neovitae.common.blockentity.routing;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import com.breakinblocks.neovitae.client.event.RoutingBeamHandler;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.block.BlockRoutingNode;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datamap.RoutingNodeHelper;
import com.breakinblocks.neovitae.common.menu.MasterRoutingNodeMenu;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.api.routing.*;
import com.breakinblocks.neovitae.common.routing.*;
import com.breakinblocks.neovitae.util.Constants;

import java.util.*;
import java.util.Map.Entry;
import net.minecraft.world.level.chunk.LevelChunk;

public class MasterRoutingNodeBlockEntity extends BlockEntity implements IMasterRoutingNode, Container, MenuProvider {

    private static final int TREE_OFFSET = 10;

    public static final int SLOT_STACK_UPGRADE = 0;
    public static final int SLOT_SPEED_UPGRADE = 1;

    public static final int ENERGY_RATE_DEFAULT = 500;
    /** Hard storage cap; effective rate is further clamped by {@link #getUpgradeEnergyCeiling()}. */
    public static final int ENERGY_RATE_MAX = 1_000_000;
    public static final int ENERGY_RATE_MIN = 0;

    private static final int PHANTOM_SWEEP_INTERVAL_TICKS = 1200;

    private int currentInput;
    private int configuredEnergyRate = ENERGY_RATE_DEFAULT;
    /** Authoritative adjacency map; BFS over this drives connectivity without loading any BEs. */
    private TreeMap<BlockPos, List<BlockPos>> connectionMap = new TreeMap<>();
    private List<BlockPos> generalNodeList = new ArrayList<>();
    private boolean legacyMigrationAttempted = false;
    private int ticksSincePhantomSweep = 0;

    private List<BlockPos> clientRenderNeighbors = new ArrayList<>();
    private boolean pendingClientSync = false;

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

    @Override
    public void onLoad() {
        super.onLoad();
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

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        if (pendingClientSync) {
            pendingClientSync = false;
            BlockState bs = level.getBlockState(pos);
            level.sendBlockUpdated(pos, bs, bs, 3);
        }

        currentInput = level.getDirectSignalTo(pos);

        // Sweep runs independently of channel tick rate so a slow master still self-heals.
        if (++ticksSincePhantomSweep >= PHANTOM_SWEEP_INTERVAL_TICKS) {
            ticksSincePhantomSweep = 0;
            sweepPhantomEntries(level);
        }

        int tickMod = RoutingNodeHelper.getEffectiveTickRate(
                getBlockState().getBlock(),
                getItem(SLOT_SPEED_UPGRADE).getCount()
        );
        if (level.getGameTime() % tickMod != 0) return;

        // One-shot rebuild for pre-persistence saves that only stored the graph on nodes.
        if (!legacyMigrationAttempted) {
            legacyMigrationAttempted = true;
            if (connectionMap.isEmpty() && !generalNodeList.isEmpty()) {
                rebuildConnectionMapFromLoadedNodes(level);
            }
        }

        for (RoutingChannel<?> channel : RoutingChannelRegistry.getChannels()) {
            processChannel(channel, level);
        }
    }

    /**
     * Prune graph entries whose chunks are loaded but no longer hold a routing-node block.
     * Catches WorldEdit fast-mode, region-file deletion, and any destructive op that
     * bypasses {@code Block.onRemove}. Unloaded positions are left alone.
     */
    private void sweepPhantomEntries(Level level) {
        if (connectionMap.isEmpty()) return;

        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : connectionMap.keySet()) {
            if (pos.equals(worldPosition)) continue;
            if (!level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) continue;

            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BlockRoutingNode)) {
                toRemove.add(pos);
            }
        }

        if (toRemove.isEmpty()) return;

        for (BlockPos pos : toRemove) {
            removeNodeFromGraph(pos);
            generalNodeList.remove(pos);
            for (List<BlockPos> list : inputNodeLists.values()) list.remove(pos);
            for (List<BlockPos> list : outputNodeLists.values()) list.remove(pos);
        }
        setChanged();
    }

    /** Best-effort: rebuild {@link #connectionMap} from whatever nodes are currently loaded. */
    private void rebuildConnectionMapFromLoadedNodes(Level level) {
        for (BlockPos nodePos : new ArrayList<>(generalNodeList)) {
            BlockEntity tile = level.getBlockEntity(nodePos);
            if (!(tile instanceof IRoutingNode node)) continue;
            for (BlockPos neighbor : node.getConnected()) {
                addConnection(nodePos, neighbor);
            }
        }
    }

    /** Emergency rebuild from live BEs in range. Preserves upgrade/energy settings. */
    public int rescanNetwork(int radius) {
        Level lvl = getLevel();
        if (lvl == null) return 0;

        connectionMap.clear();
        generalNodeList.clear();
        inputNodeLists.values().forEach(List::clear);
        outputNodeLists.values().forEach(List::clear);

        int added = 0;
        int r = Math.max(1, radius);
        int radiusSq = r * r;

        int minCx = (worldPosition.getX() - r) >> 4;
        int maxCx = (worldPosition.getX() + r) >> 4;
        int minCz = (worldPosition.getZ() - r) >> 4;
        int maxCz = (worldPosition.getZ() + r) >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                if (!lvl.hasChunk(cx, cz)) continue;
                LevelChunk chunk = lvl.getChunk(cx, cz);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof IRoutingNode node)) continue;
                    if (be instanceof IMasterRoutingNode && !entry.getKey().equals(worldPosition)) continue;
                    BlockPos nodePos = entry.getKey();
                    if (nodePos.distSqr(worldPosition) > radiusSq) continue;

                    if (!nodePos.equals(worldPosition)) {
                        addNodeToList(node);
                        added++;
                    }
                    for (BlockPos neighbor : node.getConnected()) {
                        addConnection(nodePos, neighbor);
                    }
                    if (!(be instanceof IMasterRoutingNode)) {
                        node.connectMasterToRemainingNode(lvl, new LinkedList<>(), this);
                    }
                }
            }
        }

        setChanged();
        return added;
    }

    public boolean graphContains(BlockPos pos) {
        return pos.equals(worldPosition) || connectionMap.containsKey(pos);
    }

    /** BFS over connectionMap; never loads block entities during traversal. */
    public boolean isConnectedViaGraph(BlockPos startPos) {
        if (startPos.equals(this.worldPosition)) return true;
        if (!connectionMap.containsKey(startPos)) return false;

        BlockPos target = this.worldPosition;
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            if (cur.equals(target)) return true;

            // Respect redstone-gating only when the node is loaded; unloaded nodes can't be receiving signals.
            if (level != null && level.hasChunk(cur.getX() >> 4, cur.getZ() >> 4)) {
                BlockEntity tile = level.getBlockEntity(cur);
                if (tile instanceof IRoutingNode node && !node.isConnectionEnabled(cur)) {
                    continue;
                }
            }

            List<BlockPos> neighbors = connectionMap.get(cur);
            if (neighbors == null) continue;
            for (BlockPos n : neighbors) {
                if (visited.add(n)) {
                    queue.add(n);
                }
            }
        }
        return false;
    }

    private record FilterEntry<F extends IRoutingFilter>(BlockPos nodePos, F filter) {}

    @SuppressWarnings("unchecked")
    private <F extends IRoutingFilter> void processChannel(RoutingChannel<F> channel, Level level) {
        List<BlockPos> outputNodes = getOutputList(channel.id());
        List<BlockPos> inputNodes = getInputList(channel.id());

        Map<Integer, List<FilterEntry<F>>> outputMap = new TreeMap<>();
        for (BlockPos outputPos : outputNodes) {
            BlockEntity tile = level.getBlockEntity(outputPos);
            if (tile != null && isConnectedViaGraph(outputPos)) {
                for (Direction facing : Direction.values()) {
                    if (!channel.isConnectedOnSide(tile, facing) || !channel.isOutputSide(tile, facing)) continue;

                    F filter = channel.getOutputFilter(tile, facing);
                    if (filter != null) {
                        int priority = channel.getPriority(tile, facing);
                        outputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>())
                                .add(new FilterEntry<>(outputPos, filter));
                    }
                }
            }
        }

        Map<Integer, List<FilterEntry<F>>> inputMap = new TreeMap<>();
        for (BlockPos inputPos : inputNodes) {
            BlockEntity tile = level.getBlockEntity(inputPos);
            if (tile != null && isConnectedViaGraph(inputPos)) {
                for (Direction facing : Direction.values()) {
                    if (!channel.isConnectedOnSide(tile, facing) || !channel.isInputSide(tile, facing)) continue;

                    F filter = channel.getInputFilter(tile, facing);
                    if (filter != null) {
                        int priority = channel.getPriority(tile, facing);
                        inputMap.computeIfAbsent(TREE_OFFSET - priority, k -> new ArrayList<>())
                                .add(new FilterEntry<>(inputPos, filter));
                    }
                }
            }
        }

        int maxTransfer = channel.getMaxTransfer(this);

        for (Entry<Integer, List<FilterEntry<F>>> outputEntry : outputMap.entrySet()) {
            for (FilterEntry<F> outEntry : outputEntry.getValue()) {
                for (Entry<Integer, List<FilterEntry<F>>> inputEntry : inputMap.entrySet()) {
                    for (FilterEntry<F> inEntry : inputEntry.getValue()) {
                        int transferred = channel.transfer(inEntry.filter(), outEntry.filter(), maxTransfer);
                        if (transferred > 0) {
                            spawnActivityParticles(level, outEntry.nodePos(), inEntry.nodePos());
                        }
                        maxTransfer -= transferred;
                        if (maxTransfer <= 0) return;
                    }
                }
            }
        }
    }

    private static void spawnActivityParticles(Level level, BlockPos outputNodePos, BlockPos inputNodePos) {
        if (!(level instanceof ServerLevel sl)) return;
        ColoredParticleOptions outputGlow = new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFF8744, true);
        ColoredParticleOptions inputGlow = new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x4488FF, true);
        sl.sendParticles(outputGlow,
                outputNodePos.getX() + 0.5, outputNodePos.getY() + 0.6, outputNodePos.getZ() + 0.5,
                4, 0.25, 0.25, 0.25, 0.0);
        sl.sendParticles(inputGlow,
                inputNodePos.getX() + 0.5, inputNodePos.getY() + 0.6, inputNodePos.getZ() + 0.5,
                4, 0.25, 0.25, 0.25, 0.0);
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

    /** Raw value the player has entered in the UI ("I want up to this much per pulse"). */
    public int getConfiguredEnergyRate() {
        return configuredEnergyRate;
    }

    public void setConfiguredEnergyRate(int rate) {
        int clamped = Math.max(ENERGY_RATE_MIN, Math.min(ENERGY_RATE_MAX, rate));
        if (clamped != this.configuredEnergyRate) {
            this.configuredEnergyRate = clamped;
            setChanged();
        }
    }

    /** Ceiling derived from installed stack upgrades via the datamap formula. */
    public int getUpgradeEnergyCeiling() {
        return RoutingNodeHelper.getEffectiveEnergyTransfer(
                getBlockState().getBlock(),
                getItem(SLOT_STACK_UPGRADE).getCount()
        );
    }

    /** The configured throttle clamped by the upgrade-derived ceiling. */
    public int getEffectiveEnergyRate() {
        return Math.min(configuredEnergyRate, getUpgradeEnergyCeiling());
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("configuredEnergyRate", configuredEnergyRate);
        savePosList(tag, Constants.NBT.ROUTING_MASTER_GENERAL, generalNodeList);

        ValueOutput.TypedOutputList<BlockPos> graphList = tag.list("connectionGraph", BlockPos.CODEC);
        // Graph encoding: flatten connection map to pairs of (key, neighbor) with the key repeated.
        // Simpler encoding as list-of-child-compounds.
        ValueOutput.ValueOutputList graphChildren = tag.childrenList("connectionGraph");
        for (Entry<BlockPos, List<BlockPos>> entry : connectionMap.entrySet()) {
            ValueOutput entryOut = graphChildren.addChild();
            BlockPos key = entry.getKey();
            entryOut.putInt("kx", key.getX());
            entryOut.putInt("ky", key.getY());
            entryOut.putInt("kz", key.getZ());
            ValueOutput.ValueOutputList neighbors = entryOut.childrenList("n");
            for (BlockPos neighbor : entry.getValue()) {
                ValueOutput nb = neighbors.addChild();
                nb.putInt("x", neighbor.getX());
                nb.putInt("y", neighbor.getY());
                nb.putInt("z", neighbor.getZ());
            }
        }

        for (RoutingChannel<?> channel : RoutingChannelRegistry.getChannels()) {
            savePosList(tag, "channel_input_" + channel.id(), getInputList(channel.id()));
            savePosList(tag, "channel_output_" + channel.id(), getOutputList(channel.id()));
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        ContainerHelper.loadAllItems(tag, items);
        this.configuredEnergyRate = Math.max(ENERGY_RATE_MIN, Math.min(ENERGY_RATE_MAX,
                tag.getIntOr("configuredEnergyRate", ENERGY_RATE_DEFAULT)));
        generalNodeList = loadPosList(tag, Constants.NBT.ROUTING_MASTER_GENERAL);

        connectionMap.clear();
        tag.childrenList("connectionGraph").ifPresent(list -> {
            for (ValueInput entryTag : list) {
                BlockPos key = new BlockPos(
                        entryTag.getIntOr("kx", 0),
                        entryTag.getIntOr("ky", 0),
                        entryTag.getIntOr("kz", 0));
                List<BlockPos> neighbors = new ArrayList<>();
                entryTag.childrenList("n").ifPresent(nbList -> {
                    for (ValueInput nbTag : nbList) {
                        neighbors.add(new BlockPos(
                                nbTag.getIntOr("x", 0),
                                nbTag.getIntOr("y", 0),
                                nbTag.getIntOr("z", 0)));
                    }
                });
                connectionMap.put(key, neighbors);
            }
        });
        legacyMigrationAttempted = false;

        clientRenderNeighbors.clear();
        tag.childrenList("renderNeighbors").ifPresent(list -> {
            for (ValueInput posTag : list) {
                clientRenderNeighbors.add(new BlockPos(
                        posTag.getIntOr("x", 0),
                        posTag.getIntOr("y", 0),
                        posTag.getIntOr("z", 0)));
            }
        });

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

    private void migrateOldKey(ValueInput tag, String oldKey, String channelId, boolean isInput) {
        List<BlockPos> oldList = loadPosList(tag, oldKey);
        if (oldList.isEmpty()) return;

        List<BlockPos> target = isInput ? getInputList(channelId) : getOutputList(channelId);
        for (BlockPos pos : oldList) {
            if (!target.contains(pos)) {
                target.add(pos);
            }
        }
    }

    private void savePosList(ValueOutput tag, String key, List<BlockPos> list) {
        if (list.isEmpty()) return;
        ValueOutput.ValueOutputList children = tag.childrenList(key);
        for (BlockPos pos : list) {
            ValueOutput posOut = children.addChild();
            posOut.putInt(Constants.NBT.X_COORD, pos.getX());
            posOut.putInt(Constants.NBT.Y_COORD, pos.getY());
            posOut.putInt(Constants.NBT.Z_COORD, pos.getZ());
        }
    }

    private List<BlockPos> loadPosList(ValueInput tag, String key) {
        List<BlockPos> list = new ArrayList<>();
        tag.childrenList(key).ifPresent(children -> {
            for (ValueInput blockTag : children) {
                list.add(new BlockPos(
                        blockTag.getIntOr(Constants.NBT.X_COORD, 0),
                        blockTag.getIntOr(Constants.NBT.Y_COORD, 0),
                        blockTag.getIntOr(Constants.NBT.Z_COORD, 0)));
            }
        });
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
        if (pos1.equals(worldPosition) || pos2.equals(worldPosition)) {
            pendingClientSync = true;
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
        if (pos1.equals(worldPosition) || pos2.equals(worldPosition)) {
            pendingClientSync = true;
        }
        setChanged();
    }

    @Override
    public void removeNodeFromGraph(BlockPos pos) {
        List<BlockPos> neighbors = connectionMap.remove(pos);
        if (neighbors != null) {
            for (BlockPos neighbor : neighbors) {
                List<BlockPos> neighborList = connectionMap.get(neighbor);
                if (neighborList != null) {
                    neighborList.remove(pos);
                    if (neighborList.isEmpty()) connectionMap.remove(neighbor);
                }
            }
            if (neighbors.contains(worldPosition) || pos.equals(worldPosition)) {
                pendingClientSync = true;
            }
        }
        // Defensive scrub for any stale references not captured via neighbors above.
        connectionMap.values().forEach(list -> list.remove(pos));
        setChanged();
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
        if (level != null && level.isClientSide()) {
            return clientRenderNeighbors;
        }
        return connectionMap.getOrDefault(getBlockPos(), List.of());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        ListTag list = new ListTag();
        for (BlockPos pos : connectionMap.getOrDefault(getBlockPos(), List.of())) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            list.add(posTag);
        }
        tag.put("renderNeighbors", list);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
        return new MasterRoutingNodeMenu(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.master_routing_node");
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        removeAllConnections();
        super.preRemoveSideEffects(pos, state);
    }

}
