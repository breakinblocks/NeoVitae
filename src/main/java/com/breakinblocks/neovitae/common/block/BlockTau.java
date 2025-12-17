package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.Supplier;

/**
 * Tau is a blood-powered crop that grows by damaging nearby living entities.
 * - Weak Tau: Grows normally but has a chance to transform into Strong Tau when damaging entities
 * - Strong Tau: Only grows when it successfully damages a living entity (requires blood sacrifice)
 */
public class BlockTau extends CropBlock {
    public final boolean isStrong;
    private Supplier<Block> strongTauSupplier;

    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public static final double TRANSFORM_CHANCE = 0.2d;

    public BlockTau(BlockBehaviour.Properties properties, boolean isStrong) {
        super(properties);
        this.isStrong = isStrong;
    }

    /**
     * Sets the supplier for the strong tau block (needed for weak -> strong transformation)
     */
    public BlockTau setStrongTauSupplier(Supplier<Block> supplier) {
        this.strongTauSupplier = supplier;
        return this;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.asItem();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[getAge(state)];
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }

        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                float growthSpeed = getGrowthSpeed(state, level, pos);

                if (random.nextInt((int) (25.0F / growthSpeed) + 1) == 0) {
                    tryGrow(level, pos, state, age, random);
                }
            }
        }
    }

    /**
     * Attempts to grow the tau crop by damaging nearby entities.
     */
    private void tryGrow(Level level, BlockPos pos, BlockState state, int currentAge, RandomSource random) {
        boolean doTransform = false;
        boolean doGrow = !isStrong; // Weak tau can grow without blood, strong tau needs blood

        // Check for nearby living entities to damage
        AABB boundingBox = new AABB(pos).inflate(1, 0, 1);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox);

        for (LivingEntity entity : entities) {
            if (entity.hurt(entity.damageSources().cactus(), 2)) {
                if (isStrong) {
                    // Strong tau only grows when it successfully damages something
                    doGrow = true;
                    break;
                } else if (random.nextDouble() <= TRANSFORM_CHANCE) {
                    // Weak tau has a chance to transform into strong tau
                    doTransform = true;
                    break;
                }
            }
        }

        if (doGrow) {
            int newAge = Math.min(currentAge + 1, getMaxAge());

            if (doTransform && strongTauSupplier != null) {
                // Transform weak tau into strong tau
                Block strongTau = strongTauSupplier.get();
                if (strongTau instanceof BlockTau strongTauBlock) {
                    level.setBlock(pos, strongTauBlock.getStateForAge(newAge), Block.UPDATE_CLIENTS);
                }
            } else {
                level.setBlock(pos, this.getStateForAge(newAge), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int age = this.getAge(state);
        if (age < this.getMaxAge()) {
            int newAge = age + this.getBonemealAgeIncrease(level);
            newAge = Math.min(newAge, this.getMaxAge());

            boolean doTransform = false;
            boolean doGrow = !isStrong;

            // Check for nearby living entities
            AABB boundingBox = new AABB(pos);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox);

            for (LivingEntity entity : entities) {
                if (entity.hurt(entity.damageSources().cactus(), 2)) {
                    if (isStrong) {
                        doGrow = true;
                        break;
                    } else if (level.random.nextDouble() <= TRANSFORM_CHANCE) {
                        doTransform = true;
                        break;
                    }
                }
            }

            if (doGrow) {
                if (doTransform && strongTauSupplier != null) {
                    Block strongTau = strongTauSupplier.get();
                    if (strongTau instanceof BlockTau strongTauBlock) {
                        level.setBlock(pos, strongTauBlock.getStateForAge(newAge), Block.UPDATE_CLIENTS);
                    }
                } else {
                    level.setBlock(pos, this.getStateForAge(newAge), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 1, 1);
    }

}
