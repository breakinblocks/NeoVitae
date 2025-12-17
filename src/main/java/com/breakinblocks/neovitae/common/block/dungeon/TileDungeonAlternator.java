package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.BaseTile;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;

/**
 * Tile entity for the Dungeon Alternator block.
 * Toggles the active state at regular intervals to create a pulsing redstone signal.
 */
public class TileDungeonAlternator extends BaseTile {

    public static final int DEFAULT_PULSE_RATE = 40; // 2 seconds (40 ticks)

    private int tickCounter = 0;
    private int pulseRate = DEFAULT_PULSE_RATE;

    public TileDungeonAlternator(BlockPos pos, BlockState state) {
        super(BMTiles.DUNGEON_ALTERNATOR_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileDungeonAlternator tile) {
        if (level.isClientSide) {
            return;
        }

        tile.tickCounter++;
        if (tile.tickCounter >= tile.pulseRate) {
            tile.tickCounter = 0;
            boolean currentActive = state.getValue(BlockAlternator.ACTIVE);
            level.setBlock(pos, state.setValue(BlockAlternator.ACTIVE, !currentActive), Block.UPDATE_ALL);
        }
    }

    public int getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(int rate) {
        this.pulseRate = Math.max(1, rate);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("tickCounter", tickCounter);
        tag.putInt("pulseRate", pulseRate);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tickCounter = tag.getInt("tickCounter");
        pulseRate = tag.contains("pulseRate") ? tag.getInt("pulseRate") : DEFAULT_PULSE_RATE;
    }
}
