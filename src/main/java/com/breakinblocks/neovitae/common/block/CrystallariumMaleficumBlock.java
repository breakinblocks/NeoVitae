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
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.CrystallariumMaleficumBlockEntity;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import javax.annotation.Nullable;

public class CrystallariumMaleficumBlock extends BaseEntityBlock {

    public static final MapCodec<CrystallariumMaleficumBlock> CODEC = simpleCodec(p -> new CrystallariumMaleficumBlock());

    public CrystallariumMaleficumBlock() {
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
        return new CrystallariumMaleficumBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return BlockEntityHelper.getTicker(blockEntityType, NVTiles.CRYSTALLARIUM_MALEFICUM_TYPE.get(), CrystallariumMaleficumBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
