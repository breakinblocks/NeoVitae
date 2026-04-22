package com.breakinblocks.neovitae.api.will;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.SpiritusChunk;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

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
    public double getCurrentWill(Level level, BlockPos pos, SpiritusType type) {
        return WorldSpiritusHandler.getCurrentWill(level, pos, type);
    }

    @Override
    public double getTotalWill(Level level, BlockPos pos) {
        return WorldSpiritusHandler.getTotalWill(level, pos);
    }

    @Override
    public double getMaxWill(Level level, BlockPos pos, SpiritusType type) {
        SpiritusChunk willChunk = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        return willChunk.getMaxWill(type);
    }

    @Override
    public double getBaseMaxWill(SpiritusType type) {
        return NeoVitae.SERVER_CONFIG.getBaseMaxWill(type);
    }

    @Override
    public double getMaxBonus(Level level, BlockPos pos, SpiritusType type) {
        SpiritusChunk willChunk = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        return willChunk.getMaxBonus(type);
    }

    @Override
    public void setMaxBonus(Level level, BlockPos pos, SpiritusType type, double bonus) {
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

    @Override
    public double addMaxBonus(Level level, BlockPos pos, SpiritusType type, double amount) {
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

    @Override
    public double addWill(Level level, BlockPos pos, SpiritusType type, double amount) {
        return WorldSpiritusHandler.addWillToChunk(level, pos, type, amount);
    }

    @Override
    public double drainWill(Level level, BlockPos pos, SpiritusType type, double amount) {
        return WorldSpiritusHandler.drainWillFromChunk(level, pos, type, amount);
    }

    @Override
    public double fillWillToAmount(Level level, BlockPos pos, SpiritusType type, double targetAmount) {
        return WorldSpiritusHandler.fillWillToAmount(level, pos, type, targetAmount);
    }

    @Override
    public SpiritusType getDominantWillType(Level level, BlockPos pos) {
        return WorldSpiritusHandler.getDominantWillType(level, pos);
    }

    @Override
    public boolean hasWill(Level level, BlockPos pos) {
        return WorldSpiritusHandler.hasWill(level, pos);
    }

    @Override
    public double getFillRatio(Level level, BlockPos pos, SpiritusType type) {
        SpiritusChunk willChunk = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        return willChunk.getFillRatio(type);
    }

    @Override
    public double transferWill(Level level, ChunkPos fromChunk, ChunkPos toChunk, SpiritusType type, double maxTransfer) {
        return WorldSpiritusHandler.transferWill(level, fromChunk, toChunk, type, maxTransfer);
    }
}
