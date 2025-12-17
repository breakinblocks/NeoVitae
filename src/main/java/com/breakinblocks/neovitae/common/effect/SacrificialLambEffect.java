package com.breakinblocks.neovitae.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.entity.goal.SacrificialLambMeleeAttackGoal;

/**
 * Sacrificial Lamb effect - makes passive mobs aggressive toward monsters
 * and explode when close to their target.
 */
public class SacrificialLambEffect extends MobEffect {

    public SacrificialLambEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof PathfinderMob animal)) {
            return true;
        }

        TargetGoal goal = new NearestAttackableTargetGoal<>(animal, Monster.class, false);
        MeleeAttackGoal attackGoal = new SacrificialLambMeleeAttackGoal(animal, 2.0D, false);

        animal.targetSelector.addGoal(2, goal);
        animal.goalSelector.addGoal(2, attackGoal);

        if (animal.getTarget() != null && animal.distanceToSqr(animal.getTarget()) < 4) {
            var effect = animal.getEffect(BMMobEffects.SACRIFICIAL_LAMB);
            float radius = effect != null ? 2 + effect.getAmplifier() * 1.5f : 2;

            animal.level().explode(null,
                    animal.getX(),
                    animal.getY() + (double) (animal.getBbHeight() / 16.0F),
                    animal.getZ(),
                    radius, false, Level.ExplosionInteraction.NONE);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
