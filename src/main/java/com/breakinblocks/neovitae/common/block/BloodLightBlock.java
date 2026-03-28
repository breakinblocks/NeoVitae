package com.breakinblocks.neovitae.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import org.jetbrains.annotations.Nullable;

public class BloodLightBlock extends BaseEntityBlock {

    public static final MapCodec<BloodLightBlock> CODEC = simpleCodec(p -> new BloodLightBlock());

    public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 1, 15);

    protected static final VoxelShape BODY = Block.box(7, 7, 7, 9, 9, 9);

    public static final int DEFAULT_BRIGHTNESS = 15;

    public BloodLightBlock() {
        super(Properties.of()
                .noCollission()
                .noOcclusion()
                .instabreak()
                .lightLevel(state -> state.getValue(BRIGHTNESS))
                .replaceable()
                .noLootTable());
        registerDefaultState(stateDefinition.any().setValue(BRIGHTNESS, DEFAULT_BRIGHTNESS));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BRIGHTNESS);
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
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloodLightBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int color = ColorHelper.fromDye(net.minecraft.world.item.DyeColor.RED);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BloodLightBlockEntity ble) {
            color = ColorHelper.fromDye(ble.getColor());
        }

        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.5 + random.nextGaussian() * 0.03;
            double y = pos.getY() + 0.45 + random.nextGaussian() * 0.03;
            double z = pos.getZ() + 0.5 + random.nextGaussian() * 0.03;

            double vx = random.nextGaussian() * 0.003;
            double vy = random.nextFloat() * 0.008;
            double vz = random.nextGaussian() * 0.003;

            level.addParticle(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color), x, y, z, vx, vy, vz);
        }

        if (random.nextInt(3) == 0) {
            level.addParticle(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                    pos.getX() + 0.5, pos.getY() + 0.49, pos.getZ() + 0.5,
                    0, 0, 0);
        }
    }
}
