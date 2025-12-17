package com.breakinblocks.neovitae.will;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import com.breakinblocks.neovitae.common.dataattachment.BMDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.network.WillChunkSyncPayload;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for managing demon will aura in the world.
 * Will is stored per-chunk using data attachments on the server.
 * Client-side cache is maintained via network sync.
 */
public class WorldDemonWillHandler {
    // Client-side cache for will data (keyed by chunk position as long)
    private static final Map<Long, WillChunk> clientCache = new ConcurrentHashMap<>();

    /**
     * Converts chunk coordinates to a single long key.
     */
    private static long chunkKey(int x, int z) {
        return ChunkPos.asLong(x, z);
    }

    /**
     * Updates the client-side cache with synced data.
     * Called from network handler.
     */
    public static void updateClientCache(int chunkX, int chunkZ, WillChunk willChunk) {
        clientCache.put(chunkKey(chunkX, chunkZ), willChunk);
    }

    /**
     * Clears the client cache (called when disconnecting).
     */
    public static void clearClientCache() {
        clientCache.clear();
    }

    /**
     * Gets the WillChunk for the chunk at the given position.
     */
    public static WillChunk getWillChunk(Level level, BlockPos pos) {
        if (level == null) {
            return new WillChunk();
        }

        ChunkPos chunkPos = new ChunkPos(pos);

        if (level.isClientSide()) {
            // Client side - read from cache
            return clientCache.getOrDefault(chunkKey(chunkPos.x, chunkPos.z), new WillChunk());
        }

        LevelChunk chunk = level.getChunkAt(pos);
        return chunk.getData(BMDataAttachments.WILL_CHUNK);
    }

    /**
     * Gets the WillChunk for the given chunk position.
     */
    public static WillChunk getWillChunk(Level level, ChunkPos chunkPos) {
        if (level == null) {
            return new WillChunk();
        }

        if (level.isClientSide()) {
            return clientCache.getOrDefault(chunkKey(chunkPos.x, chunkPos.z), new WillChunk());
        }

        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        return chunk.getData(BMDataAttachments.WILL_CHUNK);
    }

    /**
     * Gets the amount of will of a specific type in the chunk at the given position.
     */
    public static double getCurrentWill(Level level, BlockPos pos, EnumWillType type) {
        return getWillChunk(level, pos).getWill(type);
    }

    /**
     * Adds will to the chunk at the given position.
     * Note: This does NOT sync to clients immediately. Client sync happens via ItemDemonWillGauge.
     * @return The amount actually added
     */
    public static double addWillToChunk(Level level, BlockPos pos, EnumWillType type, double amount) {
        if (level == null || level.isClientSide() || amount <= 0) {
            return 0;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(BMDataAttachments.WILL_CHUNK);
        double added = willChunk.addWill(type, amount);

        if (added > 0) {
            // Create a copy to ensure NeoForge detects the change
            WillChunk newWillChunk = willChunk.copy();
            chunk.setData(BMDataAttachments.WILL_CHUNK, newWillChunk);
            chunk.setUnsaved(true);
        }

        return added;
    }

    /**
     * Drains will from the chunk at the given position.
     * Note: This does NOT sync to clients immediately. Client sync happens via ItemDemonWillGauge.
     * @return The amount actually drained
     */
    public static double drainWillFromChunk(Level level, BlockPos pos, EnumWillType type, double amount) {
        if (level == null || level.isClientSide() || amount <= 0) {
            return 0;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(BMDataAttachments.WILL_CHUNK);
        double drained = willChunk.drainWill(type, amount);

        if (drained > 0) {
            // Create a copy to ensure NeoForge detects the change
            WillChunk newWillChunk = willChunk.copy();
            chunk.setData(BMDataAttachments.WILL_CHUNK, newWillChunk);
            chunk.setUnsaved(true);
        }

        return drained;
    }

    /**
     * Syncs will chunk data to all players tracking that chunk.
     * @return The number of players synced to
     */
    public static int syncChunkToTrackingPlayers(ServerLevel level, ChunkPos chunkPos, WillChunk willChunk) {
        WillChunkSyncPayload payload = WillChunkSyncPayload.fromWillChunk(chunkPos.x, chunkPos.z, willChunk);

        // Count players tracking this chunk
        List<ServerPlayer> trackingPlayers = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
        int playerCount = trackingPlayers.size();

        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload);

        return playerCount;
    }

    /**
     * Syncs will chunk data to a specific player.
     * Used when a player starts tracking a chunk.
     */
    public static void syncChunkToPlayer(ServerPlayer player, ChunkPos chunkPos, WillChunk willChunk) {
        WillChunkSyncPayload payload = WillChunkSyncPayload.fromWillChunk(chunkPos.x, chunkPos.z, willChunk);
        PacketDistributor.sendToPlayer(player, payload);
    }

    /**
     * Sends the demon will aura at the player's current position to the player.
     * Called periodically by the Demon Will Gauge item (every 50 ticks like 1.20.1).
     * This is the primary way clients receive will updates.
     */
    public static void sendPlayerDemonWillAura(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        BlockPos pos = player.blockPosition();
        ChunkPos chunkPos = new ChunkPos(pos);
        WillChunk willChunk = getWillChunk(player.level(), pos);

        syncChunkToPlayer(player, chunkPos, willChunk);
    }

    /**
     * Fills will in the chunk up to the specified amount.
     * @return The amount actually added
     */
    public static double fillWillToAmount(Level level, BlockPos pos, EnumWillType type, double targetAmount) {
        double current = getCurrentWill(level, pos, type);
        if (current >= targetAmount) {
            return 0;
        }
        return addWillToChunk(level, pos, type, targetAmount - current);
    }

    /**
     * Gets the total will of all types in the chunk.
     */
    public static double getTotalWill(Level level, BlockPos pos) {
        return getWillChunk(level, pos).getTotalWill();
    }

    /**
     * Gets the dominant will type in the chunk.
     */
    public static EnumWillType getDominantWillType(Level level, BlockPos pos) {
        return getWillChunk(level, pos).getDominantType();
    }

    /**
     * Checks if the chunk has any will.
     */
    public static boolean hasWill(Level level, BlockPos pos) {
        return getWillChunk(level, pos).hasWill();
    }

    /**
     * Transfers will from one chunk to an adjacent chunk.
     * Used by demon pylons.
     * Note: This does NOT sync to clients immediately. Client sync happens via ItemDemonWillGauge.
     * @return The amount actually transferred
     */
    public static double transferWill(Level level, ChunkPos fromChunk, ChunkPos toChunk, EnumWillType type, double maxTransfer) {
        if (level == null || level.isClientSide()) {
            return 0;
        }

        // Get will amounts in both chunks
        LevelChunk from = level.getChunk(fromChunk.x, fromChunk.z);
        LevelChunk to = level.getChunk(toChunk.x, toChunk.z);

        WillChunk fromWill = from.getData(BMDataAttachments.WILL_CHUNK);
        WillChunk toWill = to.getData(BMDataAttachments.WILL_CHUNK);

        double fromAmount = fromWill.getWill(type);
        double toAmount = toWill.getWill(type);

        // Only transfer if source has more than destination
        if (fromAmount <= toAmount) {
            return 0;
        }

        // Calculate how much to transfer (equalize, but limited by maxTransfer)
        double difference = fromAmount - toAmount;
        double toTransfer = Math.min(maxTransfer, difference / 2);

        // Also limited by destination capacity (uses configurable max)
        double toMaxWill = toWill.getMaxWill(type);
        double toCapacity = toMaxWill - toAmount;
        toTransfer = Math.min(toTransfer, toCapacity);

        if (toTransfer <= 0) {
            return 0;
        }

        // Perform transfer
        fromWill.drainWill(type, toTransfer);
        toWill.addWill(type, toTransfer);

        // Create copies to ensure NeoForge detects the changes
        WillChunk newFromWill = fromWill.copy();
        WillChunk newToWill = toWill.copy();

        from.setData(BMDataAttachments.WILL_CHUNK, newFromWill);
        to.setData(BMDataAttachments.WILL_CHUNK, newToWill);
        from.setUnsaved(true);
        to.setUnsaved(true);

        return toTransfer;
    }

    /**
     * Gets the maximum will capacity for a specific type in the chunk.
     * This includes both the base config value and any per-chunk bonuses.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The maximum will capacity
     */
    public static double getMaxWill(Level level, BlockPos pos, EnumWillType type) {
        return getWillChunk(level, pos).getMaxWill(type);
    }

    /**
     * Gets the per-chunk bonus to maximum will capacity for a specific type.
     *
     * @param level The level
     * @param pos   The position (chunk is determined from this)
     * @param type  The will type
     * @return The bonus capacity (0 if none)
     */
    public static double getMaxBonus(Level level, BlockPos pos, EnumWillType type) {
        return getWillChunk(level, pos).getMaxBonus(type);
    }

    /**
     * Sets the per-chunk bonus to maximum will capacity for a specific type.
     * This is used by rituals to expand chunk capacity.
     *
     * <p>Server-side only. Does nothing on client.</p>
     *
     * @param level  The level
     * @param pos    The position (chunk is determined from this)
     * @param type   The will type
     * @param bonus  The new bonus value (must be >= 0)
     */
    public static void setMaxBonus(Level level, BlockPos pos, EnumWillType type, double bonus) {
        if (level == null || level.isClientSide()) {
            return;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(BMDataAttachments.WILL_CHUNK);
        willChunk.setMaxBonus(type, bonus);

        // Create a copy to ensure NeoForge detects the change
        WillChunk newWillChunk = willChunk.copy();
        chunk.setData(BMDataAttachments.WILL_CHUNK, newWillChunk);
        chunk.setUnsaved(true);
    }

    /**
     * Adds to the per-chunk bonus to maximum will capacity for a specific type.
     * This is used by rituals to expand chunk capacity.
     *
     * <p>Server-side only. Returns current bonus on client.</p>
     *
     * @param level  The level
     * @param pos    The position (chunk is determined from this)
     * @param type   The will type
     * @param amount The amount to add (can be negative to reduce)
     * @return The new bonus value
     */
    public static double addMaxBonus(Level level, BlockPos pos, EnumWillType type, double amount) {
        if (level == null || level.isClientSide()) {
            return getMaxBonus(level, pos, type);
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(BMDataAttachments.WILL_CHUNK);
        double newBonus = willChunk.addMaxBonus(type, amount);

        // Create a copy to ensure NeoForge detects the change
        WillChunk newWillChunk = willChunk.copy();
        chunk.setData(BMDataAttachments.WILL_CHUNK, newWillChunk);
        chunk.setUnsaved(true);

        return newBonus;
    }
}
