package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.common.block.type.PillarCapType;

import javax.annotation.Nullable;

/**
 * Inversion Pillar Cap block - decorative cap for the inversion pillar.
 * Can be placed in top or bottom orientation based on placement context.
 */
public class BlockInversionPillarEnd extends Block {

    public static final EnumProperty<PillarCapType> TYPE = EnumProperty.create("type", PillarCapType.class);

    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0, 0, 0, 16, 8, 16);
    protected static final VoxelShape TOP_SHAPE = Block.box(0, 8, 0, 16, 16, 16);

    public BlockInversionPillarEnd(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, PillarCapType.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();

        // If clicking the top face or below midpoint, place bottom cap
        if (clickedFace == Direction.UP) {
            return this.defaultBlockState().setValue(TYPE, PillarCapType.BOTTOM);
        } else if (clickedFace == Direction.DOWN) {
            return this.defaultBlockState().setValue(TYPE, PillarCapType.TOP);
        }

        // For side clicks, determine based on click location within the block
        double y = context.getClickLocation().y - clickedPos.getY();
        if (y < 0.5) {
            return this.defaultBlockState().setValue(TYPE, PillarCapType.BOTTOM);
        }
        return this.defaultBlockState().setValue(TYPE, PillarCapType.TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case TOP -> TOP_SHAPE;
            case BOTTOM -> BOTTOM_SHAPE;
        };
    }
}
