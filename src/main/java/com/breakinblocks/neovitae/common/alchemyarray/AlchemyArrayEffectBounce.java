package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class AlchemyArrayEffectBounce extends AlchemyArrayEffect {

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity.isShiftKeyDown()) {
            entity.fallDistance = 0;
            return;
        }

        Vec3 motion = entity.getDeltaMovement();
        if (motion.y < 0) {
            double bounceMultiplier = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(motion.x, -motion.y * bounceMultiplier, motion.z);
            entity.fallDistance = 0;
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x44BB22),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.3, 0.1, 0.3, 0.05);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectBounce();
    }
}
