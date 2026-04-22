package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import net.minecraft.world.phys.Vec3;

/**
 * Melee attack AI for Daemonium Cruoris (ghoul).
 * - Within 5 blocks: random claw left/right (30/30) or grave leap (40%, requires running + cooldown)
 * - Grave leap has 7s cooldown and only triggers while running
 * - Falls back to claw attacks when leap is unavailable
 */
public class DaemoniumCruorisAttackGoal extends Goal {
    private final DaemoniumCruorisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double MELEE_RANGE = 5.0;
    private static final double LEAP_RANGE = 10.0;

    public DaemoniumCruorisAttackGoal(DaemoniumCruorisEntity mob) {
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
            if (!mob.getDeltaMovement().equals(Vec3.ZERO)) {
                // Allow movement during leap
            } else {
                mob.getNavigation().stop();
            }
            return;
        }

        // Pathfind toward target
        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);
            double speed = mob.isRunning() ? 1.5D : 1.0D;
            mob.getNavigation().moveTo(target, speed);
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 5) return;

        // Grave leap: running + within 10 blocks + 40% chance
        if (dist <= LEAP_RANGE && dist > 3.0 && mob.canLeap() && mob.getRandom().nextFloat() < 0.4F) {
            mob.performLeapAttack(target);
            return;
        }

        // Claw attacks: within 5 blocks
        if (dist <= MELEE_RANGE) {
            if (mob.getRandom().nextBoolean()) {
                mob.performClawAttack(target, true);
            } else {
                mob.performClawAttack(target, false);
            }
        }
    }
}
