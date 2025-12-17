package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Spectral Block Tile Entity - tracks the original fluid state and duration.
 * When duration expires, restores the original fluid.
 */
public class SpectralBlockTile extends BaseTile {

    public static final int DEFAULT_DURATION = 40; // 2 seconds default
    public static final int MAX_DURATION = 100; // 5 seconds max

    private BlockState containedBlockState = Blocks.WATER.defaultBlockState();
    private int duration = DEFAULT_DURATION;

    public SpectralBlockTile(BlockPos pos, BlockState state) {
        super(BMTiles.SPECTRAL_BLOCK_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpectralBlockTile tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.duration--;

        if (tile.duration <= 0) {
            // Restore the original block
            tile.restoreContainedBlock();
        }
    }

    /**
     * Restores the original fluid block and removes this spectral block.
     */
    public void restoreContainedBlock() {
        if (level != null && !level.isClientSide()) {
            BlockState toRestore = containedBlockState;
            if (toRestore != null && !toRestore.isAir()) {
                level.setBlock(worldPosition, toRestore, Block.UPDATE_ALL);
            } else {
                // If no contained state, just remove
                level.removeBlock(worldPosition, false);
            }
        }
    }

    /**
     * Sets the block state that this spectral block is replacing.
     */
    public void setContainedBlockState(BlockState state) {
        this.containedBlockState = state;
        setChanged();
    }

    /**
     * Gets the block state contained within this spectral block.
     */
    public BlockState getContainedBlockState() {
        return containedBlockState;
    }

    /**
     * Resets the duration timer. Called by the sigil to keep the block suppressed.
     */
    public void resetDuration() {
        this.duration = DEFAULT_DURATION;
        setChanged();
    }

    /**
     * Resets the duration with a specific value.
     */
    public void resetDuration(int newDuration) {
        this.duration = Math.min(newDuration, MAX_DURATION);
        setChanged();
    }

    /**
     * Gets the remaining duration.
     */
    public int getDuration() {
        return duration;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("duration", duration);
        if (containedBlockState != null) {
            tag.put("containedState", NbtUtils.writeBlockState(containedBlockState));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        duration = tag.getInt("duration");
        if (tag.contains("containedState")) {
            containedBlockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("containedState"));
        }
    }
}
