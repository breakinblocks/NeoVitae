package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;

public abstract class AlchemyArrayEffect {
    public abstract AlchemyArrayEffect getNewCopy();

    public abstract void readFromNBT(CompoundTag compound);

    public abstract void writeToNBT(CompoundTag compound);

    public abstract boolean update(AlchemyArrayBlockEntity array, int activeCounter);

    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level world, BlockPos pos, BlockState state, Entity entity) {
    }

    public int getRedstoneSignal(AlchemyArrayBlockEntity tile) {
        return 0;
    }

    public boolean isSignalSource() {
        return false;
    }
}
