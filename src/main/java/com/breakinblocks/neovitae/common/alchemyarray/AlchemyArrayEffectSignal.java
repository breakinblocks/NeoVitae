package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

public class AlchemyArrayEffectSignal extends AlchemyArrayEffect {

    private int lastSignal = 0;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        if (ticksActive % 10 != 0) return false;

        int newSignal = calculateSignal(tile);
        if (newSignal != lastSignal) {
            lastSignal = newSignal;
            BlockPos pos = tile.getBlockPos();
            level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
        }

        return false;
    }

    private int calculateSignal(AlchemyArrayBlockEntity tile) {
        Binding binding = tile.getOwnerBinding();
        if (binding.isEmpty()) return 0;

        Anima network = AnimaHelper.getAnima(binding);
        if (network == null) return 0;

        int currentEV = network.getCurrentEV();
        if (currentEV <= 0) return 0;
        if (currentEV >= 1000000) return 15;

        return 1 + (int) (14.0 * currentEV / 1000000.0);
    }

    @Override
    public int getRedstoneSignal(AlchemyArrayBlockEntity tile) {
        return lastSignal;
    }

    @Override
    public boolean isSignalSource() {
        return true;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("lastSignal", lastSignal);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        lastSignal = tag.getInt("lastSignal");
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectSignal();
    }
}
