package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.will.WorldSpiritusHandler;

public class AlchemyArrayEffectSpiritSiphon extends AlchemyArrayEffect {

    private static final float DAMAGE = 2.0F;
    private static final double SPIRITUS_PER_HIT = 0.5;
    private static final int COOLDOWN_TICKS = 20;
    private int cooldown = 0;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        if (cooldown > 0) cooldown--;
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level level, BlockPos pos,
                                          BlockState state, Entity entity) {
        if (level.isClientSide()) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (entity instanceof Player) return;
        if (cooldown > 0) return;

        living.hurtServer((ServerLevel) living.level(), living.damageSources().cactus(), DAMAGE);
        cooldown = COOLDOWN_TICKS;

        WorldSpiritusHandler.addWillToChunk(level, pos, SpiritusType.RAW, SPIRITUS_PER_HIT);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0xAA0000),
                    entity.getX(), entity.getY() + 0.3, entity.getZ(), 4, 0.2, 0.1, 0.2, 0.02);
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x8844CC),
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.2, 0.3, 0.2, 0.02);
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putInt("cooldown", cooldown);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        cooldown = tag.getIntOr("cooldown", 0);
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectSpiritSiphon();
    }
}
