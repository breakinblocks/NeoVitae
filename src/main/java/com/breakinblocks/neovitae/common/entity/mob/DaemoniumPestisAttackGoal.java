package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Melee attack AI for Daemonium Pestis (shadow spider).
 * - Fang bite at close range
 * - Shadow lunge at medium range
 * All attacks apply Poison.
 */
public class DaemoniumPestisAttackGoal extends Goal {
    private final DaemoniumPestisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    public DaemoniumPestisAttackGoal(DaemoniumPestisEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = mob.getTarget();
        if (t == null || !t.isAlive()) return false;
        this.target = t;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity t = mob.getTarget();
        if (t == null || !t.isAlive()) return false;
        this.target = t;
        return true;
    }

    @Override
    public void stop() { this.target = null; this.seeTime = 0; }

    @Override
    public boolean requiresUpdateEveryTick() { return true; }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) return;

        double dist = mob.distanceTo(target);
        boolean canSee = mob.getSensing().hasLineOfSight(target);

        if (canSee) seeTime++;
        else seeTime = 0;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (mob.isAttacking()) {
            mob.getNavigation().stop();
            return;
        }

        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);
            mob.getNavigation().moveTo(target, 1.2D);
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 5) return;

        if (dist <= 3.0) {
            if (mob.getRandom().nextFloat() < 0.6F) {
                mob.performFangAttack(target);
            } else {
                mob.performLungeAttack(target);
            }
        } else if (dist <= 5.0 && mob.getRandom().nextFloat() < 0.3F) {
            mob.performLungeAttack(target);
        }
    }
}
