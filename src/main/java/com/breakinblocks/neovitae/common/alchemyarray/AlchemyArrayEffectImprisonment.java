package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.event.ImprisonmentArrayHandler;

public class AlchemyArrayEffectImprisonment extends AlchemyArrayEffect {

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) {
            return false;
        }
        ImprisonmentArrayHandler.register(level, tile.getBlockPos());
        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectImprisonment();
    }
}
