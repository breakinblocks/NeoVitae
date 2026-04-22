package com.breakinblocks.neovitae.common.routing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import com.breakinblocks.neovitae.api.routing.IMasterRoutingNode;
import com.breakinblocks.neovitae.api.routing.IRoutingNode;
import com.breakinblocks.neovitae.api.stream.StreamPresets;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class RoutingLinkHelper {

    public static final int AUTO_BIND_RADIUS = 16;

    private RoutingLinkHelper() {}

    public static void fireLinkBolt(Level level, BlockPos from, BlockPos to) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        StreamPresets.arcaneBolt(from, to).build()
                .sendToNearby(serverLevel, from, 128);
    }

    /**
     * Preference order on placement:
     *   1. Nearest {@link IMasterRoutingNode} in range.
     *   2. Nearest non-master node in range that is already bound to a living master.
     *      Candidates are walked closest-first and orphans (masterPos == ZERO) and
     *      stale bindings (recorded master no longer resolves) are skipped, falling
     *      through to the next closest candidate until one binds or the list runs out.
     *
     * Orphan non-master nodes are never picked, so two freshly placed nodes within
     * range of each other will both stay unbound until a bound node or master is in
     * range.
     *
     * @return the position bound to, or {@code null} if the node was left unbound.
     */
    @Nullable
    public static BlockPos tryAutoBind(Level level, BlockPos placedPos, IRoutingNode placedNode) {
        if (level.isClientSide()) return null;
        if (placedNode instanceof IMasterRoutingNode) return null;
        if (!placedNode.getMasterPos().equals(BlockPos.ZERO)) return null;

        BlockPos masterPos = findNearestMaster(level, placedPos, AUTO_BIND_RADIUS);
        if (masterPos != null) {
            BlockEntity masterTile = level.getBlockEntity(masterPos);
            if (masterTile instanceof IMasterRoutingNode master) {
                bindToMaster(level, placedNode, placedPos, master, masterPos);
                fireLinkBolt(level, placedPos, masterPos);
                return masterPos;
            }
        }

        for (BlockPos candidatePos : findNonMasterNodesByDistance(level, placedPos, AUTO_BIND_RADIUS, placedPos)) {
            BlockEntity neighborTile = level.getBlockEntity(candidatePos);
            if (!(neighborTile instanceof IRoutingNode neighborNode)) continue;
            if (neighborNode instanceof IMasterRoutingNode) continue;

            BlockPos neighborMasterPos = neighborNode.getMasterPos();
            if (neighborMasterPos.equals(BlockPos.ZERO)) continue;

            BlockEntity masterTile = level.getBlockEntity(neighborMasterPos);
            if (!(masterTile instanceof IMasterRoutingNode)) continue;

            bindThroughNeighbor(level, placedNode, placedPos, neighborNode, candidatePos);
            fireLinkBolt(level, placedPos, candidatePos);
            return candidatePos;
        }

        return null;
    }

    /** Mirrors the state changes in {@code ItemNodeRouter.connectToMaster}. */
    public static void bindToMaster(Level level, IRoutingNode node, BlockPos nodePos,
                                     IMasterRoutingNode master, BlockPos masterPos) {
        if (node.getMasterPos().equals(BlockPos.ZERO)) {
            node.connectMasterToRemainingNode(level, new LinkedList<>(), master);
            master.addNodeToList(node);
        }
        master.addConnection(nodePos, masterPos);
        node.addConnection(masterPos);
    }

    /**
     * Joins {@code unboundNode} into {@code neighborNode}'s network. Callers must
     * guarantee the neighbor is bound and its recorded master still resolves to a
     * real {@link IMasterRoutingNode}; this method assumes both.
     */
    public static void bindThroughNeighbor(Level level, IRoutingNode unboundNode, BlockPos unboundPos,
                                            IRoutingNode neighborNode, BlockPos neighborPos) {
        BlockPos neighborMasterPos = neighborNode.getMasterPos();
        BlockEntity masterTile = level.getBlockEntity(neighborMasterPos);
        if (!(masterTile instanceof IMasterRoutingNode master)) return;

        master.addConnection(unboundPos, neighborPos);
        master.addNodeToList(unboundNode);
        unboundNode.connectMasterToRemainingNode(level, new LinkedList<>(), master);
        neighborNode.addConnection(unboundPos);
        unboundNode.addConnection(neighborPos);
    }

    @Nullable
    public static BlockPos findNearestMaster(Level level, BlockPos origin, int radius) {
        int radiusSq = radius * radius;
        BlockPos bestPos = null;
        double bestDistSq = Double.MAX_VALUE;

        int minCx = (origin.getX() - radius) >> 4;
        int maxCx = (origin.getX() + radius) >> 4;
        int minCz = (origin.getZ() - radius) >> 4;
        int maxCz = (origin.getZ() + radius) >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                LevelChunk chunk = level.getChunk(cx, cz);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof IMasterRoutingNode)) continue;
                    BlockPos bePos = entry.getKey();
                    double distSq = bePos.distSqr(origin);
                    if (distSq <= radiusSq && distSq < bestDistSq) {
                        bestDistSq = distSq;
                        bestPos = bePos.immutable();
                    }
                }
            }
        }
        return bestPos;
    }

    /**
     * All non-master routing nodes within {@code radius} of {@code origin}, sorted
     * closest-first. No binding-state filter is applied here: callers must revalidate
     * each candidate as they walk the list, because state can change between placement
     * and the next tick.
     */
    public static List<BlockPos> findNonMasterNodesByDistance(Level level, BlockPos origin, int radius, BlockPos ignorePos) {
        int radiusSq = radius * radius;
        List<BlockPos> found = new ArrayList<>();

        int minCx = (origin.getX() - radius) >> 4;
        int maxCx = (origin.getX() + radius) >> 4;
        int minCz = (origin.getZ() - radius) >> 4;
        int maxCz = (origin.getZ() + radius) >> 4;

        for (int cx = minCx; cx <= maxCx; cx++) {
            for (int cz = minCz; cz <= maxCz; cz++) {
                if (!level.hasChunk(cx, cz)) continue;
                LevelChunk chunk = level.getChunk(cx, cz);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof IRoutingNode)) continue;
                    if (be instanceof IMasterRoutingNode) continue;
                    BlockPos bePos = entry.getKey();
                    if (bePos.equals(ignorePos)) continue;
                    double distSq = bePos.distSqr(origin);
                    if (distSq <= radiusSq) {
                        found.add(bePos.immutable());
                    }
                }
            }
        }

        found.sort(Comparator.comparingDouble(p -> p.distSqr(origin)));
        return found;
    }
}
