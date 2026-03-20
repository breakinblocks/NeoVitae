package com.breakinblocks.neovitae.client;

import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.will.WillChunk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side cache for demon will chunk data received from the server.
 * Cleared on disconnect and dimension change to prevent stale data and memory leaks.
 */
@OnlyIn(Dist.CLIENT)
public class ClientWillCache {

    private static final Map<Long, WillChunk> cache = new ConcurrentHashMap<>();

    public static void update(int chunkX, int chunkZ, WillChunk willChunk) {
        cache.put(ChunkPos.asLong(chunkX, chunkZ), willChunk);
    }

    public static WillChunk get(int chunkX, int chunkZ) {
        return cache.getOrDefault(ChunkPos.asLong(chunkX, chunkZ), new WillChunk());
    }

    public static void clear() {
        cache.clear();
    }
}
