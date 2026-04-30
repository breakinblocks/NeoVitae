package com.breakinblocks.neovitae.will;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.network.SpiritusSyncPayload;

import com.breakinblocks.neovitae.client.ClientSpiritusCache;

import java.util.List;

public class WorldSpiritusHandler {

    public static SpiritusChunk getSpiritusChunk(Level level, BlockPos pos) {
        if (level == null) {
            return new SpiritusChunk();
        }

        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);

        if (level.isClientSide()) {
            return ClientSpiritusCache.get(chunkPos.x(), chunkPos.z());
        }

        LevelChunk chunk = level.getChunkAt(pos);
        return chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
    }

    public static SpiritusChunk getSpiritusChunk(Level level, ChunkPos chunkPos) {
        if (level == null) {
            return new SpiritusChunk();
        }

        if (level.isClientSide()) {
            return ClientSpiritusCache.get(chunkPos.x(), chunkPos.z());
        }

        LevelChunk chunk = level.getChunk(chunkPos.x(), chunkPos.z());
        return chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
    }

    public static double getCurrentWill(Level level, BlockPos pos, SpiritusType type) {
        return getSpiritusChunk(level, pos).getSpiritus(type);
    }

    /**
     * Adds will to the chunk at the given position.
     * Note: This does NOT sync to clients immediately. Client sync happens via SpiritusGaugeItem.
     * @return The amount actually added
     */
    public static double addWillToChunk(Level level, BlockPos pos, SpiritusType type, double amount) {
        if (level == null || level.isClientSide() || amount <= 0) {
            return 0;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        SpiritusChunk willChunk = chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
        double added = willChunk.addSpiritus(type, amount);

        if (added > 0) {
            SpiritusChunk newSpiritusChunk = willChunk.copy();
            chunk.setData(NVDataAttachments.SPIRITUS_CHUNK, newSpiritusChunk);
            chunk.markUnsaved();
        }

        return added;
    }

    public static double drainWillFromChunk(Level level, BlockPos pos, SpiritusType type, double amount) {
        if (level == null || level.isClientSide() || amount <= 0) {
            return 0;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        SpiritusChunk willChunk = chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
        double drained = willChunk.drainSpiritus(type, amount);

        if (drained > 0) {
            SpiritusChunk newSpiritusChunk = willChunk.copy();
            chunk.setData(NVDataAttachments.SPIRITUS_CHUNK, newSpiritusChunk);
            chunk.markUnsaved();
        }

        return drained;
    }

    public static int syncChunkToTrackingPlayers(ServerLevel level, ChunkPos chunkPos, SpiritusChunk willChunk) {
        SpiritusSyncPayload payload = SpiritusSyncPayload.fromSpiritusChunk(chunkPos.x(), chunkPos.z(), willChunk);
        List<ServerPlayer> trackingPlayers = level.getChunkSource().chunkMap.getPlayers(chunkPos, false);
        int playerCount = trackingPlayers.size();

        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload);

        return playerCount;
    }

    public static void syncChunkToPlayer(ServerPlayer player, ChunkPos chunkPos, SpiritusChunk willChunk) {
        SpiritusSyncPayload payload = SpiritusSyncPayload.fromSpiritusChunk(chunkPos.x(), chunkPos.z(), willChunk);
        PacketDistributor.sendToPlayer(player, payload);
    }

    /**
     * Sends the spiritus aura at the player's current position to the player.
     * Called periodically by the Spiritus Gauge item (every 50 ticks like 1.20.1).
     * This is the primary way clients receive will updates.
     */
    public static void sendPlayerSpiritusAura(ServerPlayer player) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        BlockPos pos = player.blockPosition();
        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
        SpiritusChunk willChunk = getSpiritusChunk(player.level(), pos);

        syncChunkToPlayer(player, chunkPos, willChunk);
    }

    public static double fillWillToAmount(Level level, BlockPos pos, SpiritusType type, double targetAmount) {
        double current = getCurrentWill(level, pos, type);
        if (current >= targetAmount) {
            return 0;
        }
        return addWillToChunk(level, pos, type, targetAmount - current);
    }

    public static double getTotalSpiritus(Level level, BlockPos pos) {
        return getSpiritusChunk(level, pos).getTotalSpiritus();
    }

    public static SpiritusType getDominantWillType(Level level, BlockPos pos) {
        return getSpiritusChunk(level, pos).getDominantType();
    }

    public static boolean hasSpiritus(Level level, BlockPos pos) {
        return getSpiritusChunk(level, pos).hasSpiritus();
    }

    public static double transferWill(Level level, ChunkPos fromChunk, ChunkPos toChunk, SpiritusType type, double maxTransfer) {
        if (level == null || level.isClientSide()) {
            return 0;
        }

        LevelChunk from = level.getChunk(fromChunk.x(), fromChunk.z());
        LevelChunk to = level.getChunk(toChunk.x(), toChunk.z());

        SpiritusChunk fromWill = from.getData(NVDataAttachments.SPIRITUS_CHUNK);
        SpiritusChunk toWill = to.getData(NVDataAttachments.SPIRITUS_CHUNK);

        double fromAmount = fromWill.getSpiritus(type);
        double toAmount = toWill.getSpiritus(type);

        if (fromAmount <= toAmount) {
            return 0;
        }

        double difference = fromAmount - toAmount;
        double toTransfer = Math.min(maxTransfer, difference / 2);

        double toMaxWill = toWill.getMaxSpiritus(type);
        double toCapacity = toMaxWill - toAmount;
        toTransfer = Math.min(toTransfer, toCapacity);

        if (toTransfer <= 0) {
            return 0;
        }

        fromWill.drainSpiritus(type, toTransfer);
        toWill.addSpiritus(type, toTransfer);

        SpiritusChunk newFromWill = fromWill.copy();
        SpiritusChunk newToWill = toWill.copy();

        from.setData(NVDataAttachments.SPIRITUS_CHUNK, newFromWill);
        to.setData(NVDataAttachments.SPIRITUS_CHUNK, newToWill);
        from.markUnsaved();
        to.markUnsaved();

        return toTransfer;
    }

    public static double getMaxSpiritus(Level level, BlockPos pos, SpiritusType type) {
        return getSpiritusChunk(level, pos).getMaxSpiritus(type);
    }

    public static double getMaxBonus(Level level, BlockPos pos, SpiritusType type) {
        return getSpiritusChunk(level, pos).getMaxBonus(type);
    }

    public static void setMaxBonus(Level level, BlockPos pos, SpiritusType type, double bonus) {
        if (level == null || level.isClientSide()) {
            return;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        SpiritusChunk willChunk = chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
        willChunk.setMaxBonus(type, bonus);
        SpiritusChunk newSpiritusChunk = willChunk.copy();
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK, newSpiritusChunk);
        chunk.markUnsaved();
    }

    public static double addMaxBonus(Level level, BlockPos pos, SpiritusType type, double amount) {
        if (level == null || level.isClientSide()) {
            return getMaxBonus(level, pos, type);
        }

        LevelChunk chunk = level.getChunkAt(pos);
        SpiritusChunk willChunk = chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
        double newBonus = willChunk.addMaxBonus(type, amount);
        SpiritusChunk newSpiritusChunk = willChunk.copy();
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK, newSpiritusChunk);
        chunk.markUnsaved();

        return newBonus;
    }
}
