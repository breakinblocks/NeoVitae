// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AlchemyArrayEffectElevator extends AlchemyArrayEffect {

    private static final int MAX_SEARCH_DISTANCE = 64;
    private static final int COOLDOWN_TICKS = 10;
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level level, BlockPos pos,
                                          BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        long gameTime = level.getGameTime();
        Long lastUse = COOLDOWNS.get(player.getUUID());
        if (lastUse != null && gameTime - lastUse < COOLDOWN_TICKS) return;

        if (player.getDeltaMovement().y > 0.1 && !player.isShiftKeyDown()) {
            BlockPos target = findElevator(level, pos, true);
            if (target != null) {
                teleportPlayer(player, level, target);
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                COOLDOWNS.put(player.getUUID(), gameTime);
            }
        } else if (player.isShiftKeyDown()) {
            BlockPos target = findElevator(level, pos, false);
            if (target != null) {
                teleportPlayer(player, level, target);
                COOLDOWNS.put(player.getUUID(), gameTime);
            }
        }
    }

    private BlockPos findElevator(Level level, BlockPos from, boolean searchUp) {
        int step = searchUp ? 1 : -1;
        int start = from.getY() + step * 2;
        int end = searchUp
                ? Math.min(from.getY() + MAX_SEARCH_DISTANCE, level.getMaxBuildHeight())
                : Math.max(from.getY() - MAX_SEARCH_DISTANCE, level.getMinBuildHeight());

        for (int y = start; searchUp ? y <= end : y >= end; y += step) {
            BlockPos check = new BlockPos(from.getX(), y, from.getZ());
            if (level.getBlockEntity(check) instanceof AlchemyArrayBlockEntity array
                    && array.arrayEffect instanceof AlchemyArrayEffectElevator) {
                return check;
            }
        }
        return null;
    }

    private void teleportPlayer(Player player, Level level, BlockPos target) {
        if (level instanceof ServerLevel serverLevel) {
            double px = player.getX(), py = player.getY() + 0.5, pz = player.getZ();
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x8800CC),
                    px, py, pz, 12, 0.3, 0.5, 0.3, 0.02);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA00FF),
                    px, py, pz, 6, 0.2, 0.4, 0.2, 0.01);
        }
        player.teleportTo(target.getX() + 0.5, target.getY() + 1.0, target.getZ() + 0.5);
        player.fallDistance = 0;
        level.playSound(null, target, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8f, 1.2f);
        if (level instanceof ServerLevel serverLevel) {
            double tx = target.getX() + 0.5, ty = target.getY() + 1.5, tz = target.getZ() + 0.5;
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x8800CC),
                    tx, ty, tz, 12, 0.3, 0.5, 0.3, 0.02);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.RUNE_GLOW.get(), 0xAA00FF),
                    tx, ty - 0.5, tz, 4, 0.4, 0.05, 0.4, 0);
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
        return new AlchemyArrayEffectElevator();
    }
}
