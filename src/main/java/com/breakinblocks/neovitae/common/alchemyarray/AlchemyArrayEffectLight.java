package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.ArrayList;
import java.util.List;

public class AlchemyArrayEffectLight extends AlchemyArrayEffect {

    private static final int RADIUS = 3;
    private static final int LIGHT_LEVEL = 15;
    private final List<BlockPos> placedLights = new ArrayList<>();
    private boolean initialized = false;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        if (!initialized && ticksActive > 10) {
            placeLight(level, tile.getBlockPos());
            initialized = true;
        }

        if (ticksActive % 40 == 0 && level instanceof ServerLevel serverLevel) {
            BlockPos pos = tile.getBlockPos();
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFFDD44),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.01);
        }

        return false;
    }

    private void placeLight(Level level, BlockPos center) {
        BlockPos above = center.above();
        if (level.isEmptyBlock(above) || level.getBlockState(above).is(Blocks.LIGHT)) {
            BlockState lightState = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, LIGHT_LEVEL).setValue(LightBlock.WATERLOGGED, false);
            level.setBlockAndUpdate(above, lightState);
            placedLights.add(above);
        }

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (Math.abs(dx) + Math.abs(dz) > RADIUS) continue;

                BlockPos target = center.offset(dx, 1, dz);
                if (level.isEmptyBlock(target)) {
                    int dist = Math.abs(dx) + Math.abs(dz);
                    int ll = Math.max(1, LIGHT_LEVEL - dist * 3);
                    BlockState lightState = Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, ll).setValue(LightBlock.WATERLOGGED, false);
                    level.setBlockAndUpdate(target, lightState);
                    placedLights.add(target);
                }
            }
        }
    }

    public void removeLights(Level level) {
        for (BlockPos pos : placedLights) {
            if (level.getBlockState(pos).is(Blocks.LIGHT)) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
        placedLights.clear();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putBoolean("initialized", initialized);
        int[] coords = new int[placedLights.size() * 3];
        for (int i = 0; i < placedLights.size(); i++) {
            BlockPos p = placedLights.get(i);
            coords[i * 3] = p.getX();
            coords[i * 3 + 1] = p.getY();
            coords[i * 3 + 2] = p.getZ();
        }
        tag.putIntArray("lightPositions", coords);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        initialized = tag.getBooleanOr("initialized", false);
        placedLights.clear();
        int[] coords = tag.getIntArray("lightPositions").orElse(new int[0]);
        for (int i = 0; i + 2 < coords.length; i += 3) {
            placedLights.add(new BlockPos(coords[i], coords[i + 1], coords[i + 2]));
        }
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectLight();
    }
}
