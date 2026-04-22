package com.breakinblocks.neovitae.common.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.minecraft.server.level.ServerLevel;

public class FireFuseEffect extends MobEffect {

    public FireFuseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        RandomSource random = entity.level().getRandom();
        entity.level().addParticle(ParticleTypes.FLAME,
                entity.getX() + random.nextDouble() * 0.3,
                entity.getY() + random.nextDouble() * 0.3,
                entity.getZ() + random.nextDouble() * 0.3,
                0, 0.06d, 0);
        entity.level().addParticle(
                new com.breakinblocks.neovitae.client.particle.ColoredParticleOptions(
                        com.breakinblocks.neovitae.common.particle.NVParticles.BLOOD_FLAME.get(), 0xFF2200),
                entity.getX() + (random.nextDouble() - 0.5) * 0.5,
                entity.getY() + random.nextDouble() * 0.5,
                entity.getZ() + (random.nextDouble() - 0.5) * 0.5,
                0, 0.04, 0);

        int radius = amplifier + 1;

        var effect = entity.getEffect(NVMobEffects.FIRE_FUSE);
        if (effect != null && effect.getDuration() <= 3) {
            Level.ExplosionInteraction explosionMode = EventHooks.canEntityGrief(serverLevel, entity)
                    ? Level.ExplosionInteraction.TNT
                    : Level.ExplosionInteraction.NONE;
            entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(),
                    radius, false, explosionMode);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
