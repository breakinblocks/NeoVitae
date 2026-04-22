package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Spectral Block Tile Entity - tracks the original fluid state and duration.
 * When duration expires, restores the original fluid.
 */
public class SpectralBlockEntity extends BaseBlockEntity {

    public static final int DEFAULT_DURATION = 40; // 2 seconds default
    public static final int MAX_DURATION = 100; // 5 seconds max
    private static final int STAGGER_RANGE = 10; // max random jitter ticks on expiry

    private BlockState containedBlockState = Blocks.WATER.defaultBlockState();
    private int duration = DEFAULT_DURATION;

    public SpectralBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SPECTRAL_BLOCK_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpectralBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.duration--;

        if (tile.duration <= 0) {
            tile.restoreContainedBlock();
        }
    }

    /**
     * Restores the original fluid block and removes this spectral block.
     * Uses UPDATE_KNOWN_SHAPE to avoid expensive neighbor shape recalculations
     * that cause lag cascades when many spectral blocks expire at once.
     */
    public void restoreContainedBlock() {
        if (level != null && !level.isClientSide()) {
            BlockState toRestore = containedBlockState;
            if (toRestore != null && !toRestore.isAir()) {
                level.setBlock(worldPosition, toRestore, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
            } else {
                level.removeBlock(worldPosition, false);
            }
        }
    }

    public void setContainedBlockState(BlockState state) {
        this.containedBlockState = state;
        setChanged();
    }

    public BlockState getContainedBlockState() {
        return containedBlockState;
    }

    /**
     * Resets the duration timer. Called by the sigil to keep the block suppressed.
     * Only marks dirty if the duration actually changed to avoid unnecessary chunk saves.
     */
    public void resetDuration() {
        if (this.duration < DEFAULT_DURATION) {
            this.duration = DEFAULT_DURATION;
            setChanged();
        }
    }

    public void resetDuration(int newDuration) {
        int clamped = Math.min(newDuration, MAX_DURATION);
        if (this.duration != clamped) {
            this.duration = clamped;
            setChanged();
        }
    }

    /**
     * Sets an initial duration with random jitter to stagger expiry times.
     * Prevents hundreds of spectral blocks from restoring fluid on the same tick.
     */
    public void setInitialDuration() {
        if (level != null) {
            this.duration = DEFAULT_DURATION + level.getRandom().nextInt(STAGGER_RANGE);
        } else {
            this.duration = DEFAULT_DURATION;
        }
        setChanged();
    }

    public int getDuration() {
        return duration;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("duration", duration);
        tag.store("containedBlockState", BlockState.CODEC, containedBlockState);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        duration = tag.getIntOr("duration", 0);
        containedBlockState = tag.read("containedBlockState", BlockState.CODEC)
                .orElse(Blocks.WATER.defaultBlockState());
    }
}
