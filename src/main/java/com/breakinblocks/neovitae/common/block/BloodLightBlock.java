package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class BloodLightBlock extends Block {

    public static final MapCodec<BloodLightBlock> CODEC = simpleCodec(p -> new BloodLightBlock());

    public static final IntegerProperty LIFESPAN = IntegerProperty.create("lifespan", 0, 15);

    protected static final VoxelShape BODY = Block.box(7, 7, 7, 9, 9, 9);

    private static final DustParticleOptions BLOOD_PARTICLE = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 1.0f);

    public static final int DEFAULT_LIFESPAN = 15;
    public static final int TICKS_PER_DECREMENT = 400;

    public BloodLightBlock() {
        super(Properties.of()
                .noCollission()
                .noOcclusion()
                .instabreak()
                .lightLevel(state -> state.getValue(LIFESPAN) + 1)
                .replaceable()
                .noLootTable());
        registerDefaultState(stateDefinition.any().setValue(LIFESPAN, DEFAULT_LIFESPAN));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIFESPAN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BODY;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, TICKS_PER_DECREMENT);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int currentLifespan = state.getValue(LIFESPAN);

        if (currentLifespan <= 0) {
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(LIFESPAN, currentLifespan - 1), Block.UPDATE_ALL);
            level.scheduleTick(pos, this, TICKS_PER_DECREMENT);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) != 0) {
            double x = pos.getX() + 0.5 + random.nextGaussian() / 8;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + random.nextGaussian() / 8;
            level.addParticle(BLOOD_PARTICLE, x, y, z, 0, 0, 0);
        }
    }
}
