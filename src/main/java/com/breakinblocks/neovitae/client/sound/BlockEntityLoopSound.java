package com.breakinblocks.neovitae.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class BlockEntityLoopSound extends AbstractTickableSoundInstance {

    private final Level level;
    private final BlockPos pos;
    private final Predicate<BlockEntity> activeCheck;

    public BlockEntityLoopSound(SoundEvent sound, SoundSource source, float volume, Level level, BlockPos pos, Predicate<BlockEntity> activeCheck) {
        super(sound, source, RandomSource.create());
        this.level = level;
        this.pos = pos;
        this.activeCheck = activeCheck;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
        this.volume = volume;
        this.looping = true;
    }

    @Override
    public void tick() {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null || be.isRemoved() || !activeCheck.test(be)) {
            stop();
        }
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}
