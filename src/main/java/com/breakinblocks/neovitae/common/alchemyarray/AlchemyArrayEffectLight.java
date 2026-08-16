// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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
    private static final int FIRST_CHECK_TICK = 11;
    private static final int RECHECK_INTERVAL = 100;
    private final List<BlockPos> placedLights = new ArrayList<>();
    private boolean persistent = false;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        BlockPos pos = tile.getBlockPos();
        if (ticksActive == FIRST_CHECK_TICK || (ticksActive > FIRST_CHECK_TICK && ticksActive % RECHECK_INTERVAL == 0)) {
            refresh(level, pos);
        }

        if (ticksActive % 40 == 0 && level instanceof ServerLevel serverLevel && !placedLights.isEmpty()) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), persistent ? 0xFFFFAA : 0xFFDD44),
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.01);
        }

        return false;
    }

    @Override
    public void onNeighborChanged(AlchemyArrayBlockEntity tile, BlockPos neighborPos) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return;
        refresh(level, tile.getBlockPos());
    }

    private void refresh(Level level, BlockPos pos) {
        if (level.hasNeighborSignal(pos)) {
            if (!placedLights.isEmpty()) {
                clearLights(level);
            }
        } else if (placedLights.isEmpty()) {
            placeLight(level, pos);
        }
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    private void placeLight(Level level, BlockPos center) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > RADIUS) continue;

                BlockPos target = center.offset(dx, 1, dz);
                BlockState current = level.getBlockState(target);
                if (!current.isAir() && !current.is(Blocks.LIGHT)) continue;

                BlockState lightState = Blocks.LIGHT.defaultBlockState()
                        .setValue(LightBlock.LEVEL, LIGHT_LEVEL)
                        .setValue(LightBlock.WATERLOGGED, false);
                level.setBlockAndUpdate(target, lightState);
                placedLights.add(target);
            }
        }
    }

    private void clearLights(Level level) {
        for (BlockPos pos : placedLights) {
            if (level.getBlockState(pos).is(Blocks.LIGHT)) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
        placedLights.clear();
    }

    public void removeLights(Level level) {
        if (persistent) {
            placedLights.clear();
            return;
        }
        clearLights(level);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putBoolean("persistent", persistent);
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
        persistent = tag.getBooleanOr("persistent", false);
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
