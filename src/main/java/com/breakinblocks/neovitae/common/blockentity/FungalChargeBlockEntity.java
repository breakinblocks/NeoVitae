package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.tag.NVTags;

/**
 * Block entity for fungal charges.
 * Finds and breaks connected mushroom blocks (nether mushroom stems and hyphae).
 */
public class FungalChargeBlockEntity extends VeinMineChargeBlockEntity {

    public FungalChargeBlockEntity(BlockEntityType<?> type, int maxBlocks, BlockPos pos, BlockState state) {
        super(type, maxBlocks, pos, state);
    }

    public FungalChargeBlockEntity(int maxBlocks, BlockPos pos, BlockState state) {
        this(NVTiles.FUNGAL_CHARGE_TYPE.get(), maxBlocks, pos, state);
    }

    public FungalChargeBlockEntity(BlockPos pos, BlockState state) {
        this(128, pos, state);
    }

    @Override
    public boolean isValidBlock(BlockState originalBlockState, BlockState testState) {
        return isValidStartingBlock(testState);
    }

    @Override
    public boolean isValidStartingBlock(BlockState originalBlockState) {
        return originalBlockState.is(NVTags.Blocks.MUSHROOM_HYPHAE) || originalBlockState.is(NVTags.Blocks.MUSHROOM_STEM);
    }

    @Override
    public boolean checkDiagonals() {
        return true;
    }
}
