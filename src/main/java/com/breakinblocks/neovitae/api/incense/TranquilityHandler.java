package com.breakinblocks.neovitae.api.incense;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.datamap.TranquilityHelper;
import com.breakinblocks.neovitae.common.datamap.TranquilityValue;
import com.breakinblocks.neovitae.incense.EnumTranquilityType;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link ITranquilityHandler} that uses the
 * datamap-based tranquility system.
 */
public class TranquilityHandler implements ITranquilityHandler {

    public static final TranquilityHandler INSTANCE = new TranquilityHandler();

    private TranquilityHandler() {
        // Singleton
    }

    @Override
    @Nullable
    public EnumTranquilityType getTranquilityType(Block block) {
        TranquilityValue value = TranquilityHelper.getTranquilityValue(block);
        return value != null ? value.type() : null;
    }

    @Override
    @Nullable
    public EnumTranquilityType getTranquilityType(BlockState state) {
        return getTranquilityType(state.getBlock());
    }

    @Override
    public double getTranquilityValue(Block block) {
        return TranquilityHelper.getTranquilityAmount(block);
    }

    @Override
    public double getTranquilityValue(BlockState state) {
        return getTranquilityValue(state.getBlock());
    }

    @Override
    public boolean hasTranquility(Block block) {
        return TranquilityHelper.hasTranquility(block);
    }

    @Override
    public boolean hasTranquility(BlockState state) {
        return hasTranquility(state.getBlock());
    }
}
