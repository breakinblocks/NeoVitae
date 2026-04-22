package com.breakinblocks.neovitae.common.blockentity.base;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.BaseBlockEntity;

/**
 * Base class for block entities that tick.
 * Allows disabling the ticking programmatically.
 */
public abstract class TickingBlockEntity extends BaseBlockEntity {
    private int ticksExisted;
    private boolean shouldTick = true;

    public TickingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public final void tick() {
        if (shouldTick()) {
            ticksExisted++;
            onUpdate();
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        this.ticksExisted = tag.getIntOr("ticksExisted", 0);
        this.shouldTick = tag.getBooleanOr("shouldTick", false);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
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
