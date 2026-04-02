package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Two-phase melee AI for Daemonium Doloris (wendigo).
 * P1 (>50% HP): combo1/combo2/leap
 * P2 (<50% HP): adds combo3/howl, attacks hit harder
 */
public class DaemoniumDolorisAttackGoal extends Goal {
    private final DaemoniumDolorisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double MELEE_RANGE = 4.0;
    private static final double LEAP_RANGE = 15.0;

    public DaemoniumDolorisAttackGoal(DaemoniumDolorisEntity mob) {
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

        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);
            double speed = mob.isRunning() ? 2.0D : 1.0D;
            mob.getNavigation().moveTo(target, speed);
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        boolean p2 = mob.isPhase2();

        // Close range melee
        if (dist <= MELEE_RANGE && isInFrontArc()) {
            float roll = mob.getRandom().nextFloat();
            if (p2) {
                // P2: combo1 20%, combo2 20%, combo3 20%, howl 13%
                if (roll < 0.20F) {
                    mob.performCombo1(target);
                } else if (roll < 0.40F) {
                    mob.performCombo2(target);
                } else if (roll < 0.60F) {
                    mob.performCombo3(target);
                } else {
                    mob.performCombo1(target);
                }
            } else {
                // P1: combo1 50%, combo2 50%
                if (roll < 0.50F) {
                    mob.performCombo1(target);
                } else {
                    mob.performCombo2(target);
                }
            }
        }
        // Leap range
        else if (dist > 5.0 && dist <= LEAP_RANGE) {
            float roll = mob.getRandom().nextFloat();
            if (p2 && roll < 0.13F) {
                mob.performHowl();
            } else if (roll < 0.35F) {
                mob.performLeap(target);
            }
        }
        // Close but not facing
        else if (dist <= MELEE_RANGE) {
            mob.performCombo1(target);
        }
    }

    private boolean isInFrontArc() {
        Vec3 lookVec = mob.getViewVector(1.0F);
        Vec3 toTarget = target.position().subtract(mob.position()).normalize();
        double dot = lookVec.x * toTarget.x + lookVec.z * toTarget.z;
        return dot > 0.4226;
    }
}
