package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.IncenseAltarBlockEntity;

/**
 * The Incense Altar boosts self-sacrifice when a player stands near it.
 * Build path blocks in rings around the altar to extend its range.
 * Surround with tranquility blocks (plants, water, etc.) to increase the bonus.
 */
public class BlockIncenseAltar extends BaseEntityBlock {
    public static final MapCodec<BlockIncenseAltar> CODEC = simpleCodec(BlockIncenseAltar::new);

    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 14.0D, 12.0D);

    public BlockIncenseAltar(BlockBehaviour.Properties props) {
        super(props
                .strength(2.0F, 5.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IncenseAltarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return createTickerHelper(blockEntityType, NVTiles.INCENSE_ALTAR_TYPE.get(),
                    (lvl, pos, st, altar) -> {
                        if (altar.getIncenseAddition() > 0) {
                            com.breakinblocks.neovitae.client.sound.LoopSoundManager.tryStartLoop(
                                    com.breakinblocks.neovitae.common.NVSounds.INCENSE_AMBIENT.get(),
                                    0.15f, lvl, pos,
                                    be -> be instanceof IncenseAltarBlockEntity ia && ia.getIncenseAddition() > 0
                            );
                        }
                    });
        }
        return createTickerHelper(blockEntityType, NVTiles.INCENSE_ALTAR_TYPE.get(), IncenseAltarBlockEntity::serverTick);
    }
}
