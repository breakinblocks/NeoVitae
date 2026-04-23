package com.breakinblocks.neovitae.client;

import net.minecraft.world.level.ChunkPos;
import com.breakinblocks.neovitae.will.SpiritusChunk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache for spiritus chunk data received from the server.
 * Cleared on disconnect and dimension change to prevent stale data and memory leaks.
 */
public class ClientSpiritusCache {

    private static final Map<Long, SpiritusChunk> cache = new ConcurrentHashMap<>();

    public static void update(int chunkX, int chunkZ, SpiritusChunk willChunk) {
        cache.put((((long) chunkZ) << 32 | (chunkX & 0xFFFFFFFFL)), willChunk);
    }

    public static SpiritusChunk get(int chunkX, int chunkZ) {
        return cache.getOrDefault((((long) chunkZ) << 32 | (chunkX & 0xFFFFFFFFL)), new SpiritusChunk());
    }

    public static void clear() {
        cache.clear();
    }
}
