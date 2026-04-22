package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SoftFallEffect extends MobEffect {

    public SoftFallEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // @Override (removed: not an override in 26.1)
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.fallDistance = 0;
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
