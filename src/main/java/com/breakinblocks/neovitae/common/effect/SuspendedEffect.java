package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Suspended effect - makes entity float in place by disabling gravity.
 */
public class SuspendedEffect extends MobEffect {

    public static List<LivingEntity> noGravityList = new ArrayList<>();

    public SuspendedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!noGravityList.contains(entity)) {
            noGravityList.add(entity);
            entity.setNoGravity(true);
        } else if (entity.getEffect(BMMobEffects.SUSPENDED) != null
                && entity.getEffect(BMMobEffects.SUSPENDED).getDuration() <= 1) {
            noGravityList.remove(entity);
            entity.setNoGravity(false);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (!noGravityList.contains(entity)) {
            noGravityList.add(entity);
            entity.setNoGravity(true);
        }
    }
}
