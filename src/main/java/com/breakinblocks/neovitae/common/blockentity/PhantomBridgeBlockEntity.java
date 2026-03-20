package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Phantom Bridge Tile Entity - tracks duration and the original block state.
 * When duration expires, the phantom bridge block is removed (replaced with air or original block).
 */
public class PhantomBridgeBlockEntity extends BaseBlockEntity {

    public static final int DEFAULT_DURATION = 80; // 4 seconds default (matches original)
    public static final int MAX_DURATION = 200; // 10 seconds max

    private BlockState containedBlockState = Blocks.AIR.defaultBlockState();
    private int duration = DEFAULT_DURATION;

    public PhantomBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.PHANTOM_BRIDGE_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PhantomBridgeBlockEntity tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.duration--;

        if (tile.duration <= 0) {
            tile.removeBlock();
        }
    }

    public void removeBlock() {
        if (level != null && !level.isClientSide()) {
            BlockState toRestore = containedBlockState;
            if (toRestore != null && !toRestore.isAir()) {
                level.setBlock(worldPosition, toRestore, Block.UPDATE_ALL);
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
     * Resets the duration timer to default. Called by the sigil/ritual to keep the block active.
     */
    public void resetDuration() {
        this.duration = DEFAULT_DURATION;
        setChanged();
    }

    public void resetDuration(int newDuration) {
        this.duration = Math.min(newDuration, MAX_DURATION);
        setChanged();
    }

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
