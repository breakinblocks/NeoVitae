package com.breakinblocks.neovitae.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class LoopSoundManager {

    private static final Set<BlockPos> ACTIVE_LOOPS = new HashSet<>();

    public static void tryStartLoop(SoundEvent sound, float volume, Level level, BlockPos pos, Predicate<BlockEntity> activeCheck) {
        if (sound == null) return;
        if (ACTIVE_LOOPS.contains(pos)) return;

        BlockEntityLoopSound loopSound = new BlockEntityLoopSound(sound, SoundSource.BLOCKS, volume, level, pos, activeCheck) {
            @Override
            public void tick() {
                super.tick();
                if (isStopped()) {
                    ACTIVE_LOOPS.remove(pos);
                }
            }
        };

        ACTIVE_LOOPS.add(pos);
        Minecraft.getInstance().getSoundManager().play(loopSound);
    }

    public static void clear() {
        ACTIVE_LOOPS.clear();
    }
}
