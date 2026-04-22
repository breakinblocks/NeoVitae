package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class AlchemyArrayEffectFreeze extends AlchemyArrayEffect {

    private static final int RADIUS = 3;
    private int blocksProcessed = 0;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        if (ticksActive < 40) return false;

        if (ticksActive % 10 != 0) return false;

        BlockPos center = tile.getBlockPos();
        boolean didWork = false;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;

                for (int dy = -1; dy <= 1; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(target);

                    if (state.getFluidState().is(Fluids.WATER) && state.getFluidState().isSource()) {
                        level.setBlockAndUpdate(target, Blocks.ICE.defaultBlockState());
                        didWork = true;
                        blocksProcessed++;
                    } else if (state.isAir()) {
                        BlockPos below = target.below();
                        BlockState belowState = level.getBlockState(below);
                        if (belowState.isSolidRender() && !belowState.is(Blocks.ICE)) {
                            level.setBlockAndUpdate(target, Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 1));
                            didWork = true;
                            blocksProcessed++;
                        }
                    }
                }
            }
        }

        if (didWork && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x88CCFF),
                    center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5, 6, RADIUS * 0.3, 0.3, RADIUS * 0.3, 0.02);
        }

        return !didWork && ticksActive > 60;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("blocksProcessed", blocksProcessed);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        blocksProcessed = tag.getIntOr("blocksProcessed", 0);
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectFreeze();
    }
}
