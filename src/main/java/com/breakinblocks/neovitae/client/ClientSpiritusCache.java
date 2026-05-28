package com.breakinblocks.neovitae.client;

import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache for spiritus chunk data received from the server.
 * Cleared on disconnect and dimension change to prevent stale data and memory leaks.
 */
@OnlyIn(Dist.CLIENT)
public class ClientSpiritusCache {

    private static final Map<Long, SpiritusChunk> cache = new ConcurrentHashMap<>();

    public static void update(int chunkX, int chunkZ, SpiritusChunk spiritusChunkVar) {
        cache.put(ChunkPos.asLong(chunkX, chunkZ), spiritusChunkVar);
    }

    public static SpiritusChunk get(int chunkX, int chunkZ) {
        return cache.getOrDefault(ChunkPos.asLong(chunkX, chunkZ), new SpiritusChunk());
    }

    public static void clear() {
        cache.clear();
    }
}
