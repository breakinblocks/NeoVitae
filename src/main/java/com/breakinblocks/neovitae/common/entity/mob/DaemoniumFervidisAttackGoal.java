package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Melee brute AI for Daemonium Fervidis.
 * - Always pathfinds toward target aggressively
 * - Decay Swing (60%): melee sweep, range 3, any stance
 * - Revenant Smash (40%): leap slam, range 5-13, running stance only
 */
public class DaemoniumFervidisAttackGoal extends Goal {
    private final DaemoniumFervidisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double MELEE_RANGE = 3.0;
    private static final double SMASH_MIN = 5.0;
    private static final double SMASH_MAX = 13.0;

    public DaemoniumFervidisAttackGoal(DaemoniumFervidisEntity mob) {
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

        if (canSee) seeTime++;
        else seeTime = 0;

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (mob.isAttacking()) {
            mob.getNavigation().stop();
            return;
        }

        // Relentless pursuit
        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);
            double speed = mob.isRunning() ? 1.6D : 1.0D;
            mob.getNavigation().moveTo(target, speed);
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        if (dist <= MELEE_RANGE && isInFrontArc()) {
            // Close range: decay swing
            mob.performSwingAttack(target);
        } else if (mob.isRunning() && dist >= SMASH_MIN && dist <= SMASH_MAX) {
            // Running + mid range: revenant smash
            if (mob.getRandom().nextFloat() < 0.4F) {
                mob.performSmashAttack(target);
            }
        } else if (dist <= MELEE_RANGE) {
            // Close but not facing: still swing
            mob.performSwingAttack(target);
        }
    }

    private boolean isInFrontArc() {
        Vec3 lookVec = mob.getViewVector(1.0F);
        Vec3 toTarget = target.position().subtract(mob.position()).normalize();
        double dot = lookVec.x * toTarget.x + lookVec.z * toTarget.z;
        return dot > 0.4226; // cos(65 degrees)
    }
}
