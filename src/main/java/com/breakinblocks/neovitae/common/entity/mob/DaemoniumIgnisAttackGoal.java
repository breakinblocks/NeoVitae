package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Attack AI matching the MythicMobs routing:
 * - If target within 4.2 blocks AND in front arc → ground slam (attack_2)
 * - Otherwise → random between fireball (attack_1) and sword slash (attack_3)
 */
public class DaemoniumIgnisAttackGoal extends Goal {
    private final DaemoniumIgnisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double SLAM_RANGE = 4.2;
    private static final double FIREBALL_RANGE = 25.0;

    public DaemoniumIgnisAttackGoal(DaemoniumIgnisEntity mob) {
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

        // Don't move while attacking (self-stun)
        if (mob.isAttacking()) {
            mob.getNavigation().stop();
            return;
        }

        // Pathfind toward target
        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);
            if (dist > SLAM_RANGE * 0.8) {
                mob.getNavigation().moveTo(target, 1.0D);
            } else {
                mob.getNavigation().stop();
            }
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        if (dist <= SLAM_RANGE && isInFrontArc()) {
            // Close range + in front → ground slam
            mob.performSlamAttack(target);
        } else if (dist <= SLAM_RANGE) {
            // Close but not in front → sword slash
            mob.performSwordAttack(target);
        } else if (dist <= FIREBALL_RANGE) {
            // Ranged → random fireball or sword slash
            if (mob.getRandom().nextBoolean()) {
                mob.performFireballAttack(target);
            } else {
                mob.performSwordAttack(target);
            }
        }
    }

    private boolean isInFrontArc() {
        Vec3 lookVec = mob.getViewVector(1.0F);
        Vec3 toTarget = target.position().subtract(mob.position()).normalize();
        double dot = lookVec.x * toTarget.x + lookVec.z * toTarget.z;
        // cos(65°) ≈ 0.4226
        return dot > 0.4226;
    }
}
