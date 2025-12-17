package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Alchemy array effect that bounces entities upward when they step on it.
 * Sneaking disables the bounce effect.
 */
public class AlchemyArrayEffectBounce extends AlchemyArrayEffect {

    public AlchemyArrayEffectBounce() {
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        return false; // Doesn't complete on its own
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayTile tile, Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity.isShiftKeyDown()) {
            entity.fallDistance = 0;
            return;
        }

        Vec3 motion = entity.getDeltaMovement();
        if (motion.y < 0) {
            // Bounce effect - reverse vertical momentum
            double bounceMultiplier = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(motion.x, -motion.y * bounceMultiplier, motion.z);
            entity.fallDistance = 0;
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectBounce();
    }
}
