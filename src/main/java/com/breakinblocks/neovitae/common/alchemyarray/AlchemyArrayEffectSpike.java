package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Alchemy array effect that damages entities when they step on it.
 */
public class AlchemyArrayEffectSpike extends AlchemyArrayEffect {

    public AlchemyArrayEffectSpike() {
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        return false; // Doesn't complete on its own
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayTile tile, Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.hurt(entity.damageSources().cactus(), 2.0F);
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
        return new AlchemyArrayEffectSpike();
    }
}
