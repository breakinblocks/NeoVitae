package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import com.breakinblocks.neovitae.common.event.CommonEventHandler;

/**
 * Suspended effect - makes entity float in place by disabling gravity.
 *
 * <p>Cleanup on removal (milk, /clear, expiry) is handled by
 * {@link CommonEventHandler}.</p>
 */
public class SuspendedEffect extends MobEffect {

    public SuspendedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // @Override (removed: not an override in 26.1)
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.isNoGravity()) {
            entity.setNoGravity(true);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        entity.setNoGravity(true);
    }
}
