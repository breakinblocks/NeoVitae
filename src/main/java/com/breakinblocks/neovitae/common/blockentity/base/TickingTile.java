package com.breakinblocks.neovitae.common.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.BaseTile;

/**
 * Base class for block entities that tick.
 * Allows disabling the ticking programmatically.
 */
public abstract class TickingTile extends BaseTile {
    private int ticksExisted;
    private boolean shouldTick = true;

    public TickingTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public final void tick() {
        if (shouldTick()) {
            ticksExisted++;
            onUpdate();
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.ticksExisted = tag.getInt("ticksExisted");
        this.shouldTick = tag.getBoolean("shouldTick");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ticksExisted", getTicksExisted());
        tag.putBoolean("shouldTick", shouldTick());
    }

    /**
     * Called every tick that {@link #shouldTick()} is true.
     */
    public abstract void onUpdate();

    public int getTicksExisted() {
        return ticksExisted;
    }

    public void resetLifetime() {
        ticksExisted = 0;
    }

    public boolean shouldTick() {
        return shouldTick;
    }

    public void setShouldTick(boolean shouldTick) {
        this.shouldTick = shouldTick;
    }
}
