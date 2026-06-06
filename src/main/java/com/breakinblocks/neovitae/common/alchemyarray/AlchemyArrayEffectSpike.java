// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class AlchemyArrayEffectSpike extends AlchemyArrayEffect {

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.hurt(entity.damageSources().cactus(), 2.0F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0xAA0000),
                        entity.getX(), entity.getY() + 0.3, entity.getZ(), 4, 0.2, 0.1, 0.2, 0.02);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectSpike();
    }
}
