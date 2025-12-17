package com.breakinblocks.neovitae.common.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.damagesource.BMDamageSources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility methods for Blood Magic potion effects.
 */
public class BMPotionUtils {

    public static Random rand = new Random();

    /**
     * Damages a mob and grows surrounding plants. Used by the Plant Leech effect.
     *
     * @param entity           The entity to damage
     * @param horizontalRadius Horizontal search radius for plants
     * @param verticalRadius   Vertical search radius for plants
     * @param damageRatio      Damage dealt per plant grown
     * @param maxPlantsGrown   Maximum plants to attempt to grow
     * @return Total damage incurred
     */
    public static double damageMobAndGrowSurroundingPlants(LivingEntity entity, int horizontalRadius,
                                                           int verticalRadius, double damageRatio, int maxPlantsGrown) {
        Level level = entity.level();
        if (level.isClientSide) {
            return 0;
        }

        if (!entity.isAlive()) {
            return 0;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return 0;
        }

        double incurredDamage = 0;

        List<BlockPos> growList = new ArrayList<>();

        for (int i = 0; i < maxPlantsGrown; i++) {
            BlockPos blockPos = entity.blockPosition().offset(
                    rand.nextInt(horizontalRadius * 2 + 1) - horizontalRadius,
                    rand.nextInt(verticalRadius * 2 + 1) - verticalRadius,
                    rand.nextInt(horizontalRadius * 2 + 1) - horizontalRadius);
            BlockState state = level.getBlockState(blockPos);

            // Check if block can be grown (using BonemealableBlock)
            if (state.getBlock() instanceof BonemealableBlock) {
                growList.add(blockPos);
            }
        }

        for (BlockPos blockPos : growList) {
            BlockState preBlockState = level.getBlockState(blockPos);
            for (int n = 0; n < 10; n++) {
                BlockState currentState = level.getBlockState(blockPos);
                // Stop if the block has changed (e.g., sapling grew into a tree)
                // or if it's no longer a growable block
                if (!currentState.is(preBlockState.getBlock()) || !(currentState.getBlock() instanceof BonemealableBlock)) {
                    break;
                }
                currentState.randomTick(serverLevel, blockPos, level.random);
            }

            BlockState newState = level.getBlockState(blockPos);
            if (!newState.equals(preBlockState)) {
                level.levelEvent(2005, blockPos, 0);
                incurredDamage += damageRatio;
            }
        }

        if (incurredDamage > 0) {
            entity.hurt(entity.damageSources().source(BMDamageSources.SACRIFICE, entity), (float) incurredDamage);
        }

        return incurredDamage;
    }
}
