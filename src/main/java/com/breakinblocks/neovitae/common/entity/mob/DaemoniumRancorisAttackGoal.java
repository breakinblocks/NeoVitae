package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Ranged attack AI for Daemonium Rancoris (specter).
 * - Keeps distance from target (prefers 8-15 blocks)
 * - Spectral Bolt (70%): single ranged shot, range 15
 * - Ectoplasmic Burst (30%): AoE burst at target, range 18
 * - Retreats if target gets too close
 */
public class DaemoniumRancorisAttackGoal extends Goal {
    private final DaemoniumRancorisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double PREFERRED_MIN = 8.0;
    private static final double BOLT_RANGE = 15.0;
    private static final double BURST_RANGE = 18.0;
    private static final double TOO_CLOSE = 5.0;

    public DaemoniumRancorisAttackGoal(DaemoniumRancorisEntity mob) {
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

        // Movement: retreat if too close, approach if too far, strafe in range
        if (--pathfindDelay <= 0) {
            pathfindDelay = 4 + mob.getRandom().nextInt(7);

            if (dist < TOO_CLOSE && canSee) {
                // Back away from target
                double dx = mob.getX() - target.getX();
                double dz = mob.getZ() - target.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.01) {
                    dx /= len;
                    dz /= len;
                    mob.getNavigation().moveTo(
                            mob.getX() + dx * 8, mob.getY(), mob.getZ() + dz * 8, 1.0D);
                }
            } else if (dist > BOLT_RANGE) {
                mob.getNavigation().moveTo(target, 1.0D);
            } else {
                // In range: strafe sideways
                double strafe = (mob.getRandom().nextBoolean() ? 1 : -1) * 4.0;
                double dx = target.getX() - mob.getX();
                double dz = target.getZ() - mob.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.01) {
                    mob.getNavigation().moveTo(
                            mob.getX() + (-dz / len) * strafe,
                            mob.getY(),
                            mob.getZ() + (dx / len) * strafe,
                            0.8D);
                }
            }
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        // Choose attack based on range and randomness
        if (dist <= BURST_RANGE && mob.getRandom().nextFloat() < 0.3F) {
            mob.performBurstAttack(target);
        } else if (dist <= BOLT_RANGE) {
            mob.performBoltAttack(target);
        }
    }
}
