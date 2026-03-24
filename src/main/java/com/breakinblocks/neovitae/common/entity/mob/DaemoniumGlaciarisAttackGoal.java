package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Attack AI matching MythicMobs ice_queen routing:
 * - Randomly picks from 6 attacks based on range and cooldowns
 * - Close range (<=3): shards, mist, or wall_summon
 * - Mid range (3-12): basic or rose_toss
 * - Long range (6-25): ice_beam
 * - Mist phase every 120s (6 second cooldown in MM)
 */
public class DaemoniumGlaciarisAttackGoal extends Goal {
    private final DaemoniumGlaciarisEntity mob;
    private LivingEntity target;
    private int seeTime;
    private int pathfindDelay;
    private int mistCooldown = 0;
    private int wallCooldown = 0;

    public DaemoniumGlaciarisAttackGoal(DaemoniumGlaciarisEntity mob) {
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
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) return;

        if (mistCooldown > 0) mistCooldown--;
        if (wallCooldown > 0) wallCooldown--;

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
            if (dist > 3.0) {
                mob.getNavigation().moveTo(target, 1.0D);
            } else {
                mob.getNavigation().stop();
            }
        }

        if (!mob.canAttack()) return;
        if (!canSee || seeTime < 10) return;

        // Close range attacks (<=3 blocks)
        if (dist <= 3.0) {
            float roll = mob.getRandom().nextFloat();
            if (mistCooldown <= 0 && roll < 0.15F) {
                mob.performMistAttack();
                mistCooldown = 2400; // 120 seconds
            } else if (wallCooldown <= 0 && roll < 0.3F) {
                mob.performWallSummonAttack(target);
                wallCooldown = 600; // 30 seconds
            } else {
                mob.performShardsAttack(target);
            }
        }
        // Mid range (3-12)
        else if (dist <= 12.0) {
            if (mob.getRandom().nextBoolean()) {
                mob.performBasicAttack(target);
            } else {
                mob.performRoseTossAttack(target);
            }
        }
        // Long range (6-25)
        else if (dist <= 25.0) {
            if (mob.getRandom().nextFloat() < 0.4F) {
                mob.performIceBeamAttack(target);
            } else {
                mob.performBasicAttack(target);
            }
        }
    }
}
