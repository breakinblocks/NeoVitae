package com.breakinblocks.neovitae.api.spiritus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

/**
 * Default implementation of {@link ISpiritusHandler} that delegates to
 * {@link WorldSpiritusHandler} and chunk data attachments.
 *
 * <p>This is an internal singleton used by the API. Addon mods should not
 * instantiate this class directly; instead, use
 * {@link com.breakinblocks.neovitae.api.NeoVitaeAPI#getInstance()}{@code .getSpiritusHandler()}
 * to obtain the handler.</p>
 *
 * <p>All mutating operations (add, drain, transfer, setMaxBonus) are server-side only
 * and will silently no-op on the client.</p>
 */
public class SpiritusHandler implements ISpiritusHandler {

    public static final SpiritusHandler INSTANCE = new SpiritusHandler();

    private SpiritusHandler() {}


    @Override
    public double getCurrentSpiritus(Level level, BlockPos pos, SpiritusType type) {
        return WorldSpiritusHandler.getCurrentSpiritus(level, pos, type);
    }

    @Override
    public double getTotalSpiritus(Level level, BlockPos pos) {
        return WorldSpiritusHandler.getTotalSpiritus(level, pos);
    }

    @Override
    public double getMaxSpiritus(Level level, BlockPos pos, SpiritusType type) {
        SpiritusChunk spiritusChunkVar = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        return spiritusChunkVar.getMaxSpiritus(type);
    }

    @Override
    public double getBaseMaxSpiritus(SpiritusType type) {
        return NeoVitae.SERVER_CONFIG.getBaseMaxSpiritus(type);
    }

    @Override
    public double getMaxBonus(Level level, BlockPos pos, SpiritusType type) {
        SpiritusChunk spiritusChunkVar = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        return spiritusChunkVar.getMaxBonus(type);
    }

    @Override
    public void setMaxBonus(Level level, BlockPos pos, SpiritusType type, double bonus) {
        if (level == null || level.isClientSide()) {
            return;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        SpiritusChunk spiritusChunkVar = chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
        spiritusChunkVar.setMaxBonus(type, bonus);

        SpiritusChunk newSpiritusChunk = spiritusChunkVar.copy();
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK, newSpiritusChunk);
        chunk.setUnsaved(true);
    }

    @Override
    public double addMaxBonus(Level level, BlockPos pos, SpiritusType type, double amount) {
        if (level == null || level.isClientSide()) {
            return getMaxBonus(level, pos, type);
        }

        LevelChunk chunk = level.getChunkAt(pos);
        SpiritusChunk spiritusChunkVar = chunk.getData(NVDataAttachments.SPIRITUS_CHUNK);
        double newBonus = spiritusChunkVar.addMaxBonus(type, amount);

        SpiritusChunk newSpiritusChunk = spiritusChunkVar.copy();
        chunk.setData(NVDataAttachments.SPIRITUS_CHUNK, newSpiritusChunk);
        chunk.setUnsaved(true);

        return newBonus;
    }

    @Override
    public double addSpiritus(Level level, BlockPos pos, SpiritusType type, double amount) {
        return WorldSpiritusHandler.addSpiritusToChunk(level, pos, type, amount);
    }

    @Override
    public double drainSpiritus(Level level, BlockPos pos, SpiritusType type, double amount) {
        return WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, type, amount);
    }

    @Override
    public double fillSpiritusToAmount(Level level, BlockPos pos, SpiritusType type, double targetAmount) {
        return WorldSpiritusHandler.fillSpiritusToAmount(level, pos, type, targetAmount);
    }

    @Override
    public SpiritusType getDominantSpiritusType(Level level, BlockPos pos) {
        return WorldSpiritusHandler.getDominantSpiritusType(level, pos);
    }

    @Override
    public boolean hasSpiritus(Level level, BlockPos pos) {
        return WorldSpiritusHandler.hasSpiritus(level, pos);
    }

    @Override
    public double getFillRatio(Level level, BlockPos pos, SpiritusType type) {
        SpiritusChunk spiritusChunkVar = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        return spiritusChunkVar.getFillRatio(type);
    }

    @Override
    public double transferSpiritus(Level level, ChunkPos fromChunk, ChunkPos toChunk, SpiritusType type, double maxTransfer) {
        return WorldSpiritusHandler.transferSpiritus(level, fromChunk, toChunk, type, maxTransfer);
    }
}
