package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class BloodLanternSpawnHandler {

    private static final int RADIUS = 16;
    private static final Map<ServerLevel, LanternIndex> INDICES = new IdentityHashMap<>();

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (event.getSpawnType() == EntitySpawnReason.CHUNK_GENERATION) return;

        MobCategory category = event.getEntity().getType().getCategory();
        if (category == MobCategory.MONSTER) return;

        // World-generation accessors and off-thread mod spawns must not touch the live chunk cache.
        if (!(event.getLevel() instanceof ServerLevel level) || !level.getServer().isSameThread()) return;
        if (level.dimension().equals(DungeonDimensionHelper.DUNGEON_DIMENSION)) return;
        if (!NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.get()) {
            INDICES.remove(level);
            return;
        }

        BlockPos center = BlockPos.containing(event.getX(), event.getY(), event.getZ());
        LanternIndex index = INDICES.computeIfAbsent(level, ignored -> new LanternIndex());
        index.ensureLoadedChunks(level, center);
        if (index.hasBlockingLantern(level, center)) {
            event.setSpawnCancelled(true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        // Map updates can load many chunks in one tick. Never scan palettes in this callback:
        // the chunk may also still be undergoing promotion to FULL.
        if (event.getLevel() instanceof ServerLevel level && level.getServer().isSameThread()) {
            LanternIndex index = INDICES.get(level);
            if (index != null) index.removeChunk(event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && level.getServer().isSameThread()) {
            LanternIndex index = INDICES.get(level);
            if (index != null) index.removeChunk(event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) INDICES.remove(level);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level && level.getServer().isSameThread()
                && NeoVitae.SERVER_CONFIG.LANTERN_SPAWN_SUPPRESSION.get() && isLantern(event.getPlacedBlock())) {
            INDICES.computeIfAbsent(level, ignored -> new LanternIndex()).add(event.getPos());
        }
    }

    private static boolean isLantern(BlockState state) {
        return state.is(NVBlocks.BLOOD_LANTERN.block().get()) || state.is(NVBlocks.DEMON_LANTERN.block().get());
    }

    private static void indexChunk(ServerLevel level, LevelChunk chunk) {
        LanternIndex index = INDICES.computeIfAbsent(level, ignored -> new LanternIndex());
        index.removeChunk(chunk.getPos());
        Set<Long> positions = new LongOpenHashSet();
        Block bloodLantern = NVBlocks.BLOOD_LANTERN.block().get();
        Block demonLantern = NVBlocks.DEMON_LANTERN.block().get();
        Predicate<BlockState> lanternState = state -> state.is(bloodLantern) || state.is(demonLantern);
        LevelChunkSection[] sections = chunk.getSections();
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];
            if (section.hasOnlyAir() || !section.maybeHas(lanternState)) continue;
            int minY = level.dimensionType().minY() + sectionIndex * 16;
            for (int y = 0; y < 16; y++) for (int z = 0; z < 16; z++) for (int x = 0; x < 16; x++) {
                BlockState state = section.getBlockState(x, y, z);
                if (lanternState.test(state)) positions.add(BlockPos.asLong(chunk.getPos().getMinBlockX() + x, minY + y,
                        chunk.getPos().getMinBlockZ() + z));
            }
        }
        index.putChunk(chunk.getPos(), positions);
    }

    private static final class LanternIndex {
        private final Map<Long, Set<Long>> positionsByChunk = new HashMap<>();
        private final Set<Long> indexedChunks = new LongOpenHashSet();

        void ensureLoadedChunks(ServerLevel level, BlockPos center) {
            int chunkX = center.getX() >> 4, chunkZ = center.getZ() >> 4;
            for (int x = chunkX - 1; x <= chunkX + 1; x++) for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                long key = ChunkPos.pack(x, z);
                if (!indexedChunks.contains(key)) {
                    LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
                    if (chunk != null) indexChunk(level, chunk);
                }
            }
        }

        boolean hasBlockingLantern(ServerLevel level, BlockPos center) {
            int chunkX = center.getX() >> 4, chunkZ = center.getZ() >> 4;
            for (int x = chunkX - 1; x <= chunkX + 1; x++) for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                Set<Long> positions = positionsByChunk.get(ChunkPos.pack(x, z));
                if (positions == null) continue;
                LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
                if (chunk == null) continue;
                for (long packed : positions) {
                    BlockPos lantern = BlockPos.of(packed);
                    if (Math.abs(lantern.getX() - center.getX()) + Math.abs(lantern.getY() - center.getY())
                            + Math.abs(lantern.getZ() - center.getZ()) > RADIUS) continue;
                    BlockState state = chunk.getBlockState(lantern);
                    if (state.is(NVBlocks.BLOOD_LANTERN.block().get())) return true;
                    // Vanilla weak-power checks can read two blocks away. Skip suppression
                    // at an unloaded boundary instead of pulling more chunks into a spawn check.
                    if (state.is(NVBlocks.DEMON_LANTERN.block().get()) && hasLoadedSignalArea(level, lantern)
                            && !level.hasNeighborSignal(lantern)) return true;
                }
            }
            return false;
        }

        private boolean hasLoadedSignalArea(ServerLevel level, BlockPos pos) {
            for (int x = (pos.getX() - 2) >> 4; x <= (pos.getX() + 2) >> 4; x++) {
                for (int z = (pos.getZ() - 2) >> 4; z <= (pos.getZ() + 2) >> 4; z++) {
                    if (level.getChunkSource().getChunkNow(x, z) == null) return false;
                }
            }
            return true;
        }

        void putChunk(ChunkPos pos, Set<Long> positions) {
            long key = pos.pack(); indexedChunks.add(key);
            if (positions.isEmpty()) positionsByChunk.remove(key); else positionsByChunk.put(key, positions);
        }
        void removeChunk(ChunkPos pos) { long key = pos.pack(); indexedChunks.remove(key); positionsByChunk.remove(key); }
        void add(BlockPos pos) { long key = ChunkPos.pack(pos.getX() >> 4, pos.getZ() >> 4); positionsByChunk.computeIfAbsent(key, ignored -> new LongOpenHashSet()).add(pos.asLong()); }
        void remove(BlockPos pos) { Set<Long> positions = positionsByChunk.get(ChunkPos.pack(pos.getX() >> 4, pos.getZ() >> 4)); if (positions != null) positions.remove(pos.asLong()); }
    }
}
