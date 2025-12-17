package com.breakinblocks.neovitae.common.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Fire Fuse effect - explodes when duration expires.
 */
public class FireFuseEffect extends MobEffect {

    public FireFuseEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }

        RandomSource random = entity.level().random;
        entity.level().addParticle(ParticleTypes.FLAME,
                entity.getX() + random.nextDouble() * 0.3,
                entity.getY() + random.nextDouble() * 0.3,
                entity.getZ() + random.nextDouble() * 0.3,
                0, 0.06d, 0);

        int radius = amplifier + 1;

        var effect = entity.getEffect(BMMobEffects.FIRE_FUSE);
        if (effect != null && effect.getDuration() <= 3) {
            Level.ExplosionInteraction explosionMode = EventHooks.canEntityGrief(entity.level(), entity)
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
