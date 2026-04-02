package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * All-melee attack AI for Daemonium Corrodis.
 * - Close range + in front arc: attack_1 (single sweep) or attack_2 (double sweep)
 * - Close range + not in front: chase closer
 * - Medium range: attack_3 (two-phase slam) if off cooldown
 * All attacks apply Wither.
 */
public class DaemoniumCorrodisAttackGoal extends Goal {
    private final DaemoniumCorrodisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double MELEE_RANGE = 3.0;
    private static final double SLAM_RANGE = 4.5;

    public DaemoniumCorrodisAttackGoal(DaemoniumCorrodisEntity mob) {
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
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.pathfindDelay = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) return;

        double dist = mob.distanceTo(target);
        boolean canSee = mob.getSensing().hasLineOfSight(target);

        if (canSee) {
            seeTime++;
        } else {
            seeTime = 0;
        }

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (mob.isAttacking()) {
            mob.getNavigation().stop();
            return;
        }

        // Always pathfind toward target; this is a melee mob
        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);
            mob.getNavigation().moveTo(target, 1.0D);
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        if (dist <= MELEE_RANGE && isInFrontArc()) {
            // Close + facing: random between sweep and double sweep
            if (mob.getRandom().nextBoolean()) {
                mob.performSweepAttack(target);
            } else {
                mob.performDoubleAttack(target);
            }
        } else if (dist <= SLAM_RANGE && isInFrontArc()) {
            // Medium range: two-phase slam
            mob.performSlamAttack(target);
        } else if (dist <= MELEE_RANGE) {
            // Close but not facing: single sweep (fast, less positional)
            mob.performSweepAttack(target);
        }
    }

    private boolean isInFrontArc() {
        Vec3 lookVec = mob.getViewVector(1.0F);
        Vec3 toTarget = target.position().subtract(mob.position()).normalize();
        double dot = lookVec.x * toTarget.x + lookVec.z * toTarget.z;
        return dot > 0.4226; // cos(65°)
    }
}
