package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Alchemy array effect that launches entities upward.
 * The upward force scales with glowstone dust and feathers in the array.
 */
public class AlchemyArrayEffectUpdraft extends AlchemyArrayEffect {

    public AlchemyArrayEffectUpdraft() {
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        return false; // Doesn't complete on its own
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayTile tile, Level level, BlockPos pos, BlockState state, Entity entity) {
        double verticalMotion = 1.0;

        // Scale with glowstone (slot 1)
        int glowstoneCount = tile.getItem(1).is(Items.GLOWSTONE_DUST) ? tile.getItem(1).getCount() : 0;
        verticalMotion += 0.1 * glowstoneCount;

        // Scale with feathers (slot 0)
        int featherCount = tile.getItem(0).is(Items.FEATHER) ? tile.getItem(0).getCount() : 0;
        verticalMotion += 0.05 * featherCount;

        entity.fallDistance = 0;
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x, verticalMotion, motion.z);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectUpdraft();
    }
}
