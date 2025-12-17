package com.breakinblocks.neovitae.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;

/**
 * Melee attack goal for the Sacrificial Lamb effect.
 * The entity approaches targets but doesn't actually attack - it explodes instead.
 */
public class SacrificialLambMeleeAttackGoal extends MeleeAttackGoal {

    public SacrificialLambMeleeAttackGoal(PathfinderMob creature, double speed, boolean useLongMemory) {
        super(creature, speed, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy) {
        // Do nothing - the explosion is handled by the effect tick
    }

    @Override
    public boolean canUse() {
        return this.mob.hasEffect(BMMobEffects.SACRIFICIAL_LAMB) && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasEffect(BMMobEffects.SACRIFICIAL_LAMB) && super.canContinueToUse();
    }
}
