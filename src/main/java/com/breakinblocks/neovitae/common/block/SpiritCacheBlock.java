package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.SpiritCacheBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class SpiritCacheBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 7, 14);

    public SpiritCacheBlock(BlockBehaviour.Properties props) {
        super(props
                .strength(2.0F, 5.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpiritCacheBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof SpiritCacheBlockEntity cache) {
            cache.unpackLootTable(serverPlayer);
            level.playSound(null, pos, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            serverPlayer.openMenu(cache, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (true) {
            if (level.getBlockEntity(pos) instanceof SpiritCacheBlockEntity cache) {
                Containers.dropContents(level, pos, cache);
                level.updateNeighbourForOutputSignal(pos, this);
    }
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof SpiritCacheBlockEntity cache) || !cache.hasContents()) {
            return;
        }

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.35;
        double cz = pos.getZ() + 0.5;
        int color = 0x990011;

        for (int i = 0; i < 3; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.1;
            double oy = random.nextDouble() * 0.05;
            double oz = (random.nextDouble() - 0.5) * 0.1;
            level.addParticle(
                    new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                    cx + ox, cy + oy, cz + oz,
                    ox * 0.05, 0.01, oz * 0.05
            );
        }
    }
}
