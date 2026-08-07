package com.breakinblocks.neovitae.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

public class LoopSoundManager {

    public static final int RETRY_DELAY_TICKS = 60;

    private static final Map<BlockPos, BlockEntityLoopSound> ACTIVE_LOOPS = new HashMap<>();
    private static final Map<BlockPos, Long> RETRY_AT = new HashMap<>();

    public static void tryStartLoop(SoundEvent sound, float volume, Level level, BlockPos pos, Predicate<BlockEntity> activeCheck) {
        if (sound == null) return;

        BlockPos key = pos.immutable();
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();

        BlockEntityLoopSound existing = ACTIVE_LOOPS.get(key);
        if (existing != null) {
            if (soundManager.isActive(existing)) return;
            ACTIVE_LOOPS.remove(key);
            RETRY_AT.put(key, level.getGameTime() + RETRY_DELAY_TICKS);
            return;
        }

        Long retryAt = RETRY_AT.get(key);
        if (retryAt != null) {
            if (level.getGameTime() < retryAt) return;
            RETRY_AT.remove(key);
        }

        BlockEntityLoopSound loopSound = new BlockEntityLoopSound(sound, SoundSource.BLOCKS, volume, level, key, activeCheck);
        soundManager.play(loopSound);

        if (soundManager.isActive(loopSound)) {
            ACTIVE_LOOPS.put(key, loopSound);
        } else {
            RETRY_AT.put(key, level.getGameTime() + RETRY_DELAY_TICKS);
        }
    }

    public static void tick(Level level) {
        if (ACTIVE_LOOPS.isEmpty() && RETRY_AT.isEmpty()) return;

        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        long now = level.getGameTime();

        Iterator<Map.Entry<BlockPos, BlockEntityLoopSound>> it = ACTIVE_LOOPS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, BlockEntityLoopSound> entry = it.next();
            if (!soundManager.isActive(entry.getValue())) {
                it.remove();
                RETRY_AT.put(entry.getKey(), now + RETRY_DELAY_TICKS);
            }
        }

        RETRY_AT.values().removeIf(at -> now >= at);
    }

    public static void clear() {
        ACTIVE_LOOPS.clear();
        RETRY_AT.clear();
    }
}
