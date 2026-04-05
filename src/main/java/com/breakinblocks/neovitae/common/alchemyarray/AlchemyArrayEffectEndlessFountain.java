package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.ArrayList;
import java.util.List;

/**
 * Endless Fountain Array - continuously fills adjacent fluid tanks with water.
 *
 * <p>Every 5 ticks the array attempts to deposit up to 6 buckets (6000 mB) of water
 * into the six blocks sharing a face with it, distributing them evenly via a
 * round-robin cursor (one bucket per face when all six are tanks). Only
 * whole-bucket (1000 mB) fills are committed: if a tank cannot accept a full
 * bucket, the array moves on to the next cached tank for that bucket. If every
 * cached tank rejects a bucket the array enters an exponential backoff similar
 * to the Serenade of the Nether, capping at 80 ticks between attempts. Any
 * successful fill immediately resets the backoff.
 *
 * <p>The adjacent-tank cache is refreshed in three ways: on a timed interval
 * ({@link #CACHE_REBUILD_TICKS}), reactively whenever an adjacent block is
 * placed or broken (via {@link #onNeighborChanged}), and lazily when a cached
 * position no longer exposes a fluid handler during a deposit. Unloaded chunks
 * are respected: a cached tank in an unloaded chunk is kept (not dropped) until
 * its chunk ticks in again, and the rebuild pass skips unloaded neighbours.
 *
 * <p>The array is redstone-sensitive: any neighbouring redstone signal parks
 * the effect until the signal drops. While backing off a small puff of drowsy
 * particles hints at the slowdown so players can spot it at a glance.
 */
public class AlchemyArrayEffectEndlessFountain extends AlchemyArrayEffect {

    private static final int CYCLE_TICKS = 5;
    private static final int CACHE_REBUILD_TICKS = 60;
    private static final int BUCKETS_PER_CYCLE = 6;
    private static final int BUCKET_MB = 1000;

    // Backoff tier table: each entry is the number of 5-tick cycles to skip
    // when every cached tank rejects a full bucket. Level 0 means no skip.
    private static final int MAX_BACKOFF_LEVEL = 4;
    private static final int[] BACKOFF_CYCLES = {0, 1, 3, 7, 15};

    // Particle colours for feedback. Flow = cheerful aqua, backoff = muted slate.
    private static final int FLOW_COLOR = 0x3A9BD9;
    private static final int BACKOFF_COLOR = 0x4A5C6E;

    private final List<BlockPos> tankCache = new ArrayList<>(6);
    private int roundRobinCursor = 0;
    private int backoffLevel = 0;
    private int backoffRemaining = 0;
    private int lastCacheBuild = -1;
    private boolean cacheDirty = false;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        // Only act on our cycle cadence; the block entity still ticks every tick.
        if (ticksActive % CYCLE_TICKS != 0) return false;

        BlockPos arrayPos = tile.getBlockPos();

        // Redstone kill-switch: if any neighbour is powered, park the effect
        // but keep existing state (cache, backoff) intact.
        if (level.hasNeighborSignal(arrayPos)) {
            return false;
        }

        // Rebuild the adjacency cache on a timer, on first run, or when a
        // neighbour change has been signalled since the last rebuild.
        if (cacheDirty || lastCacheBuild < 0 || ticksActive - lastCacheBuild >= CACHE_REBUILD_TICKS) {
            rebuildCache(level, arrayPos);
            lastCacheBuild = ticksActive;
            cacheDirty = false;
        }

        // Respect an active backoff without touching any capabilities.
        if (backoffRemaining > 0) {
            backoffRemaining--;
            emitBackoffParticles(level, arrayPos);
            return false;
        }

        if (tankCache.isEmpty()) {
            // Nothing to do until the next cache rebuild.
            return false;
        }

        int placedBuckets = attemptDeposit(level);

        if (placedBuckets > 0) {
            // Reset to normal cadence and emit ambient flow particles.
            backoffLevel = 0;
            backoffRemaining = 0;
            if (level instanceof ServerLevel server) {
                server.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), FLOW_COLOR),
                        arrayPos.getX() + 0.5, arrayPos.getY() + 0.35, arrayPos.getZ() + 0.5,
                        3 * placedBuckets, 0.4, 0.1, 0.4, 0.02);
            }
        } else if (!tankCache.isEmpty()) {
            // Every cached tank refused a full bucket. Back off to reduce polling.
            if (backoffLevel < MAX_BACKOFF_LEVEL) {
                backoffLevel++;
            }
            backoffRemaining = BACKOFF_CYCLES[backoffLevel];
            emitBackoffParticles(level, arrayPos);
        }

        return false;
    }

    /**
     * Attempts to deposit up to {@link #BUCKETS_PER_CYCLE} buckets across the
     * cached tanks, spreading them evenly via round-robin. For each bucket we
     * walk the cache starting at the cursor; tanks that no longer exist are
     * removed lazily (respecting chunk-load state). Tanks that cannot take a
     * full bucket are skipped for this bucket but remain in the cache.
     *
     * @return the number of full buckets successfully deposited this cycle.
     */
    private int attemptDeposit(Level level) {
        int placed = 0;

        for (int bucket = 0; bucket < BUCKETS_PER_CYCLE && !tankCache.isEmpty(); bucket++) {
            boolean placedThisBucket = false;
            int attempts = 0;
            int cacheSize = tankCache.size();

            while (attempts < cacheSize && !tankCache.isEmpty()) {
                if (roundRobinCursor >= tankCache.size()) {
                    roundRobinCursor = 0;
                }
                BlockPos tankPos = tankCache.get(roundRobinCursor);

                // Skip (but keep) entries whose chunks have unloaded; we cannot
                // safely query them and the cache will re-examine on rebuild.
                if (!level.isLoaded(tankPos)) {
                    roundRobinCursor++;
                    attempts++;
                    continue;
                }

                IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, tankPos, null);

                if (handler == null) {
                    // Tank vanished between scans. Drop it; the cursor now points
                    // at the next tank (or falls out of range, handled above).
                    tankCache.remove(roundRobinCursor);
                    cacheSize--;
                    // Do NOT count this as an attempt; we replaced the slot we were
                    // about to try with a fresh candidate.
                    continue;
                }

                FluidStack bucketStack = new FluidStack(Fluids.WATER, BUCKET_MB);
                int simulated = handler.fill(bucketStack, IFluidHandler.FluidAction.SIMULATE);
                if (simulated >= BUCKET_MB) {
                    handler.fill(bucketStack, IFluidHandler.FluidAction.EXECUTE);
                    placed++;
                    placedThisBucket = true;
                    // Advance the cursor so the next bucket prefers the next tank.
                    roundRobinCursor++;
                    break;
                }

                // Tank exists but has no room for a full bucket; try the next.
                roundRobinCursor++;
                attempts++;
            }

            if (!placedThisBucket) {
                // No tank in the cache could take a full bucket this pass; the
                // remaining buckets in this cycle would fail the same way.
                break;
            }
        }

        return placed;
    }

    private void rebuildCache(Level level, BlockPos arrayPos) {
        tankCache.clear();
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = arrayPos.relative(dir);
            // Skip unloaded neighbours entirely; if their chunk loads before the
            // next timed rebuild, the onNeighborChanged hook (or the 60-tick
            // timer) will pick them up.
            if (!level.isLoaded(neighbor)) continue;
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, neighbor, null);
            if (handler != null) {
                tankCache.add(neighbor.immutable());
            }
        }
        // Keep the cursor in range after a shrinking rebuild.
        if (roundRobinCursor >= tankCache.size()) {
            roundRobinCursor = 0;
        }
        // Re-examine the world after a rebuild; give tanks a fresh chance even if
        // we were mid-backoff (they may have been emptied by the player).
        backoffLevel = 0;
        backoffRemaining = 0;
    }

    /**
     * Emits a small, muted particle puff to telegraph that the array is
     * currently backing off. Particle count scales with the backoff level so a
     * deeply-backed-off array looks visibly sluggish next to an active one.
     */
    private void emitBackoffParticles(Level level, BlockPos arrayPos) {
        if (!(level instanceof ServerLevel server)) return;
        // Only emit occasionally during longer backoffs to avoid overhead.
        if ((server.getGameTime() & 0xF) != 0) return;
        int count = Math.max(1, backoffLevel);
        server.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), BACKOFF_COLOR),
                arrayPos.getX() + 0.5, arrayPos.getY() + 0.2, arrayPos.getZ() + 0.5,
                count, 0.15, 0.02, 0.15, 0.0);
    }

    @Override
    public void onNeighborChanged(AlchemyArrayBlockEntity tile, BlockPos neighborPos) {
        BlockPos arrayPos = tile.getBlockPos();
        // Only react to changes on our six faces; ignore diagonals and farther
        // updates propagated through the neighbour-notification graph.
        if (neighborPos.distManhattan(arrayPos) == 1) {
            cacheDirty = true;
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("cursor", roundRobinCursor);
        tag.putInt("backoffLevel", backoffLevel);
        tag.putInt("backoffRemaining", backoffRemaining);
        tag.putInt("lastCacheBuild", lastCacheBuild);
        tag.putBoolean("cacheDirty", cacheDirty);
        ListTag cacheTag = new ListTag();
        for (BlockPos pos : tankCache) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            cacheTag.add(posTag);
        }
        tag.put("tankCache", cacheTag);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        roundRobinCursor = Math.max(0, tag.getInt("cursor"));
        backoffLevel = Math.min(Math.max(0, tag.getInt("backoffLevel")), MAX_BACKOFF_LEVEL);
        backoffRemaining = Math.max(0, tag.getInt("backoffRemaining"));
        lastCacheBuild = tag.contains("lastCacheBuild") ? tag.getInt("lastCacheBuild") : -1;
        cacheDirty = tag.getBoolean("cacheDirty");
        tankCache.clear();
        ListTag cacheTag = tag.getList("tankCache", Tag.TAG_COMPOUND);
        for (int i = 0; i < cacheTag.size(); i++) {
            CompoundTag posTag = cacheTag.getCompound(i);
            tankCache.add(new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z")));
        }
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectEndlessFountain();
    }
}
