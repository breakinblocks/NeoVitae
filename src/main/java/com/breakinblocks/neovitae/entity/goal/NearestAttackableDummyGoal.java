package com.breakinblocks.neovitae.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;

/**
 * A dummy goal used by the Passivity effect to replace hostile targeting.
 * This goal does nothing when started and only continues while the effect is active.
 */
public class NearestAttackableDummyGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public NearestAttackableDummyGoal(Mob goalOwner, Class<T> targetClass, boolean checkSight) {
        super(goalOwner, targetClass, checkSight);
    }

    @Override
    public void start() {
        // Do nothing - this is a dummy goal that prevents attacks
    }

    @Override
    public boolean canUse() {
        return this.mob.hasEffect(BMMobEffects.PASSIVITY);
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasEffect(BMMobEffects.PASSIVITY);
    }
}
