package com.breakinblocks.neovitae.will;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.network.WillChunkSyncPayload;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldDemonWillHandler {
    private static final Map<Long, WillChunk> clientCache = new ConcurrentHashMap<>();

    private static long chunkKey(int x, int z) {
        return ChunkPos.asLong(x, z);
    }

    public static void updateClientCache(int chunkX, int chunkZ, WillChunk willChunk) {
        clientCache.put(chunkKey(chunkX, chunkZ), willChunk);
    }

    public static void clearClientCache() {
        clientCache.clear();
    }

    public static WillChunk getWillChunk(Level level, BlockPos pos) {
        if (level == null) {
            return new WillChunk();
        }

        ChunkPos chunkPos = new ChunkPos(pos);

        if (level.isClientSide()) {
            return clientCache.getOrDefault(chunkKey(chunkPos.x, chunkPos.z), new WillChunk());
        }

        LevelChunk chunk = level.getChunkAt(pos);
        return chunk.getData(NVDataAttachments.WILL_CHUNK);
    }

    public static WillChunk getWillChunk(Level level, ChunkPos chunkPos) {
        if (level == null) {
            return new WillChunk();
        }

        if (level.isClientSide()) {
            return clientCache.getOrDefault(chunkKey(chunkPos.x, chunkPos.z), new WillChunk());
        }

        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        return chunk.getData(NVDataAttachments.WILL_CHUNK);
    }

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
        WillChunk willChunk = chunk.getData(NVDataAttachments.WILL_CHUNK);
        double added = willChunk.addWill(type, amount);

        if (added > 0) {
            WillChunk newWillChunk = willChunk.copy();
            chunk.setData(NVDataAttachments.WILL_CHUNK, newWillChunk);
            chunk.setUnsaved(true);
        }

        return added;
    }

    public static double drainWillFromChunk(Level level, BlockPos pos, EnumWillType type, double amount) {
        if (level == null || level.isClientSide() || amount <= 0) {
            return 0;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(NVDataAttachments.WILL_CHUNK);
        double drained = willChunk.drainWill(type, amount);

        if (drained > 0) {
            WillChunk newWillChunk = willChunk.copy();
            chunk.setData(NVDataAttachments.WILL_CHUNK, newWillChunk);
            chunk.setUnsaved(true);
        }

        return drained;
    }

    public static int syncChunkToTrackingPlayers(ServerLevel level, ChunkPos chunkPos, WillChunk willChunk) {
        WillChunkSyncPayload payload = WillChunkSyncPayload.fromWillChunk(chunkPos.x, chunkPos.z, willChunk);
        List<ServerPlayer> trackingPlayers = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
        int playerCount = trackingPlayers.size();

        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload);

        return playerCount;
    }

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

    public static double fillWillToAmount(Level level, BlockPos pos, EnumWillType type, double targetAmount) {
        double current = getCurrentWill(level, pos, type);
        if (current >= targetAmount) {
            return 0;
        }
        return addWillToChunk(level, pos, type, targetAmount - current);
    }

    public static double getTotalWill(Level level, BlockPos pos) {
        return getWillChunk(level, pos).getTotalWill();
    }

    public static EnumWillType getDominantWillType(Level level, BlockPos pos) {
        return getWillChunk(level, pos).getDominantType();
    }

    public static boolean hasWill(Level level, BlockPos pos) {
        return getWillChunk(level, pos).hasWill();
    }

    public static double transferWill(Level level, ChunkPos fromChunk, ChunkPos toChunk, EnumWillType type, double maxTransfer) {
        if (level == null || level.isClientSide()) {
            return 0;
        }

        LevelChunk from = level.getChunk(fromChunk.x, fromChunk.z);
        LevelChunk to = level.getChunk(toChunk.x, toChunk.z);

        WillChunk fromWill = from.getData(NVDataAttachments.WILL_CHUNK);
        WillChunk toWill = to.getData(NVDataAttachments.WILL_CHUNK);

        double fromAmount = fromWill.getWill(type);
        double toAmount = toWill.getWill(type);

        if (fromAmount <= toAmount) {
            return 0;
        }

        double difference = fromAmount - toAmount;
        double toTransfer = Math.min(maxTransfer, difference / 2);

        double toMaxWill = toWill.getMaxWill(type);
        double toCapacity = toMaxWill - toAmount;
        toTransfer = Math.min(toTransfer, toCapacity);

        if (toTransfer <= 0) {
            return 0;
        }

        fromWill.drainWill(type, toTransfer);
        toWill.addWill(type, toTransfer);

        WillChunk newFromWill = fromWill.copy();
        WillChunk newToWill = toWill.copy();

        from.setData(NVDataAttachments.WILL_CHUNK, newFromWill);
        to.setData(NVDataAttachments.WILL_CHUNK, newToWill);
        from.setUnsaved(true);
        to.setUnsaved(true);

        return toTransfer;
    }

    public static double getMaxWill(Level level, BlockPos pos, EnumWillType type) {
        return getWillChunk(level, pos).getMaxWill(type);
    }

    public static double getMaxBonus(Level level, BlockPos pos, EnumWillType type) {
        return getWillChunk(level, pos).getMaxBonus(type);
    }

    public static void setMaxBonus(Level level, BlockPos pos, EnumWillType type, double bonus) {
        if (level == null || level.isClientSide()) {
            return;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(NVDataAttachments.WILL_CHUNK);
        willChunk.setMaxBonus(type, bonus);
        WillChunk newWillChunk = willChunk.copy();
        chunk.setData(NVDataAttachments.WILL_CHUNK, newWillChunk);
        chunk.setUnsaved(true);
    }

    public static double addMaxBonus(Level level, BlockPos pos, EnumWillType type, double amount) {
        if (level == null || level.isClientSide()) {
            return getMaxBonus(level, pos, type);
        }

        LevelChunk chunk = level.getChunkAt(pos);
        WillChunk willChunk = chunk.getData(NVDataAttachments.WILL_CHUNK);
        double newBonus = willChunk.addMaxBonus(type, amount);
        WillChunk newWillChunk = willChunk.copy();
        chunk.setData(NVDataAttachments.WILL_CHUNK, newWillChunk);
        chunk.setUnsaved(true);

        return newBonus;
    }
}
