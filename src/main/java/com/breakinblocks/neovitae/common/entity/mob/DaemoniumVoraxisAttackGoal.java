package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Melee attack AI for Daemonium Voraxis (oni shadow).
 * Alternates between left and right slashes.
 * Drains vitality (hunger + self-heal) on hit.
 */
public class DaemoniumVoraxisAttackGoal extends Goal {
    private final DaemoniumVoraxisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;

    private static final double MELEE_RANGE = 3.0;

    public DaemoniumVoraxisAttackGoal(DaemoniumVoraxisEntity mob) {
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
    }

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
            mob.getNavigation().moveTo(target, 1.0D);
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        if (dist <= MELEE_RANGE) {
            if (mob.getRandom().nextBoolean()) {
                mob.performSlashLeft(target);
            } else {
                mob.performSlashRight(target);
            }
        }
    }
}
