package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.tag.BMTags;

/**
 * Block entity for fungal charges.
 * Finds and breaks connected mushroom blocks (nether mushroom stems and hyphae).
 */
public class FungalChargeTile extends VeinMineChargeTile {

    public FungalChargeTile(BlockEntityType<?> type, int maxBlocks, BlockPos pos, BlockState state) {
        super(type, maxBlocks, pos, state);
    }

    public FungalChargeTile(int maxBlocks, BlockPos pos, BlockState state) {
        this(BMTiles.FUNGAL_CHARGE_TYPE.get(), maxBlocks, pos, state);
    }

    public FungalChargeTile(BlockPos pos, BlockState state) {
        this(128, pos, state);
    }

    @Override
    public boolean isValidBlock(BlockState originalBlockState, BlockState testState) {
        return isValidStartingBlock(testState);
    }

    @Override
    public boolean isValidStartingBlock(BlockState originalBlockState) {
        return originalBlockState.is(BMTags.Blocks.MUSHROOM_HYPHAE) || originalBlockState.is(BMTags.Blocks.MUSHROOM_STEM);
    }

    @Override
    public boolean checkDiagonals() {
        return true;
    }
}
