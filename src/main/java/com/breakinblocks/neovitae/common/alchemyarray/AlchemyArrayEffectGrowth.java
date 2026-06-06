// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class AlchemyArrayEffectGrowth extends AlchemyArrayEffect {

    private static final int RADIUS = 2;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        if (ticksActive % 20 != 0) return false;

        BlockPos center = tile.getBlockPos();
        ServerLevel serverLevel = (ServerLevel) level;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    BlockPos target = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(target);

                    if (state.getBlock() instanceof BonemealableBlock growable) {
                        if (growable.isValidBonemealTarget(level, target, state)) {
                            if (serverLevel.random.nextFloat() < 0.15f) {
                                growable.performBonemeal(serverLevel, serverLevel.random, target, state);
                                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x44DD22),
                                        target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 2, 0.2, 0.1, 0.2, 0.01);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectGrowth();
    }
}
