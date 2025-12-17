package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.DemonPylonTile;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import javax.annotation.Nullable;

/**
 * Demon Pylon - transfers will between adjacent chunks.
 */
public class DemonPylonBlock extends BaseEntityBlock {

    public static final MapCodec<DemonPylonBlock> CODEC = simpleCodec(p -> new DemonPylonBlock());

    public DemonPylonBlock() {
        super(Properties.of()
                .strength(5.0F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DemonPylonTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, BMTiles.DEMON_PYLON_TYPE.get(), DemonPylonTile::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
