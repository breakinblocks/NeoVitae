package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Monster;
import com.breakinblocks.neovitae.entity.goal.NearestAttackableDummyGoal;

/**
 * Passivity effect - makes hostile mobs non-aggressive.
 */
public class PassivityEffect extends MobEffect {

    public PassivityEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PathfinderMob animal)) {
            return true;
        }

        TargetGoal goal = new NearestAttackableDummyGoal<>(animal, Monster.class, false);
        animal.targetSelector.addGoal(0, goal);

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
