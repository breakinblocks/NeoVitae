package com.breakinblocks.neovitae.api.will;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.BMDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.WillChunk;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

/**
 * Implementation of {@link IDemonWillHandler} that delegates to {@link WorldDemonWillHandler}.
 *
 * <p>This class provides the API implementation for addon mods to interact with
 * the demon will system.</p>
 */
public class DemonWillHandler implements IDemonWillHandler {

    public static final DemonWillHandler INSTANCE = new DemonWillHandler();

    private DemonWillHandler() {
        // Private constructor - use INSTANCE
    }

    @Override
    public double getCurrentWill(Level level, BlockPos pos, EnumWillType type) {
        return WorldDemonWillHandler.getCurrentWill(level, pos, type);
    }

    @Override
    public double getTotalWill(Level level, BlockPos pos) {
        return WorldDemonWillHandler.getTotalWill(level, pos);
    }

    @Override
    public double getMaxWill(Level level, BlockPos pos, EnumWillType type) {
        WillChunk willChunk = WorldDemonWillHandler.getWillChunk(level, pos);
        return willChunk.getMaxWill(type);
    }

    @Override
    public double getBaseMaxWill(EnumWillType type) {
        return NeoVitae.SERVER_CONFIG.getBaseMaxWill(type);
    }

    @Override
    public double getMaxBonus(Level level, BlockPos pos, EnumWillType type) {
        WillChunk willChunk = WorldDemonWillHandler.getWillChunk(level, pos);
        return willChunk.getMaxBonus(type);
    }

    @Override
    public void setMaxBonus(Level level, BlockPos pos, EnumWillType type, double bonus) {
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

    @Override
    public double addMaxBonus(Level level, BlockPos pos, EnumWillType type, double amount) {
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

    @Override
    public double addWill(Level level, BlockPos pos, EnumWillType type, double amount) {
        return WorldDemonWillHandler.addWillToChunk(level, pos, type, amount);
    }

    @Override
    public double drainWill(Level level, BlockPos pos, EnumWillType type, double amount) {
        return WorldDemonWillHandler.drainWillFromChunk(level, pos, type, amount);
    }

    @Override
    public double fillWillToAmount(Level level, BlockPos pos, EnumWillType type, double targetAmount) {
        return WorldDemonWillHandler.fillWillToAmount(level, pos, type, targetAmount);
    }

    @Override
    public EnumWillType getDominantWillType(Level level, BlockPos pos) {
        return WorldDemonWillHandler.getDominantWillType(level, pos);
    }

    @Override
    public boolean hasWill(Level level, BlockPos pos) {
        return WorldDemonWillHandler.hasWill(level, pos);
    }

    @Override
    public double getFillRatio(Level level, BlockPos pos, EnumWillType type) {
        WillChunk willChunk = WorldDemonWillHandler.getWillChunk(level, pos);
        return willChunk.getFillRatio(type);
    }

    @Override
    public double transferWill(Level level, ChunkPos fromChunk, ChunkPos toChunk, EnumWillType type, double maxTransfer) {
        return WorldDemonWillHandler.transferWill(level, fromChunk, toChunk, type, maxTransfer);
    }
}
