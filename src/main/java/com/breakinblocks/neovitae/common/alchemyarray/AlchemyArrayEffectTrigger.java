package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class AlchemyArrayEffectTrigger extends AlchemyArrayEffect {

    private int pulseTimer = 0;
    private static final int PULSE_DURATION = 10;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        if (pulseTimer > 0) {
            pulseTimer--;
            if (pulseTimer == 0) {
                Level level = tile.getLevel();
                if (level != null && !level.isClientSide) {
                    BlockPos pos = tile.getBlockPos();
                    level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
                }
            }
        }
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level level, BlockPos pos,
                                          BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (pulseTimer > 0) return;

        if (entity instanceof Player || entity instanceof Mob) {
            pulseTimer = PULSE_DURATION;
            level.updateNeighborsAt(pos, state.getBlock());

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFF4422),
                        pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 4, 0.3, 0.05, 0.3, 0.01);
            }
        }
    }

    @Override
    public int getRedstoneSignal(AlchemyArrayBlockEntity tile) {
        return pulseTimer > 0 ? 15 : 0;
    }

    @Override
    public boolean isSignalSource() {
        return true;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("pulseTimer", pulseTimer);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        pulseTimer = tag.getInt("pulseTimer");
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectTrigger();
    }
}
