package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;

import java.util.OptionalDouble;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class BurdenGroundingHandler {

    private static final double MAX_HEIGHT_ABOVE_GROUND = 1.5;
    private static final double MAX_PULL_PER_TICK = 0.8;
    private static final int GROUND_SEARCH_DEPTH = 32;
    private static final double PERCH_SNAP_DISTANCE_SQR = 9.0;

    public static void mark(Mob mob, long groundedUntil) {
        mob.setData(NVDataAttachments.BURDEN_GROUNDED_UNTIL, groundedUntil);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Mob mob) || !(mob.level() instanceof ServerLevel level)) return;
        if (!mob.hasData(NVDataAttachments.BURDEN_GROUNDED_UNTIL)) return;

        if (mob.getData(NVDataAttachments.BURDEN_GROUNDED_UNTIL) < level.getGameTime()) {
            mob.removeData(NVDataAttachments.BURDEN_GROUNDED_UNTIL);
            return;
        }
        if (!mob.isAlive()) return;

        if (mob instanceof EnderDragon dragon) {
            holdDragonOnPerch(dragon, level);
        } else {
            pullTowardGround(mob);
        }
    }

    private static void holdDragonOnPerch(EnderDragon dragon, ServerLevel level) {
        if (dragon.getDragonFight() == null) return;

        EnderDragonPhaseManager phases = dragon.getPhaseManager();
        DragonPhaseInstance phase = phases.getCurrentPhase();
        EnderDragonPhase<?> type = phase.getPhase();
        if (phase.isSitting()
                || type == EnderDragonPhase.DYING
                || type == EnderDragonPhase.HOVERING
                || type == EnderDragonPhase.LANDING
                || type == EnderDragonPhase.LANDING_APPROACH) {
            return;
        }

        Vec3 perch = Vec3.atBottomCenterOf(level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(dragon.getFightOrigin())));
        if (perch.distanceToSqr(dragon.position()) < PERCH_SNAP_DISTANCE_SQR) {
            dragon.setPos(perch);
            dragon.setDeltaMovement(Vec3.ZERO);
            phases.getPhase(EnderDragonPhase.SITTING_FLAMING).resetFlameCount();
            phases.setPhase(EnderDragonPhase.SITTING_SCANNING);
        } else {
            phases.setPhase(EnderDragonPhase.LANDING_APPROACH);
        }
    }

    private static void pullTowardGround(Mob mob) {
        if (mob instanceof WitherBoss wither && wither.getInvulnerableTicks() > 0) return;
        if (mob.onGround() || mob.isPassenger() || mob.isInWater() || mob.isInLava() || mob.onClimbable()) return;

        OptionalDouble ground = findGroundBelow(mob);
        if (ground.isEmpty()) return;

        double excess = mob.getY() - (ground.getAsDouble() + MAX_HEIGHT_ABOVE_GROUND);
        if (excess <= 0) return;

        double drop = Math.min(excess, MAX_PULL_PER_TICK);
        Vec3 motion = mob.getDeltaMovement();
        if (motion.y > 0) {
            mob.setDeltaMovement(motion.x, 0, motion.z);
        }
        if (mob.noPhysics) {
            mob.setPos(mob.getX(), mob.getY() - drop, mob.getZ());
        } else {
            mob.move(MoverType.SELF, new Vec3(0, -drop, 0));
        }
    }

    private static OptionalDouble findGroundBelow(Mob mob) {
        BlockPos.MutableBlockPos pos = mob.blockPosition().mutable();
        int bottom = Math.max(mob.level().getMinY(), pos.getY() - GROUND_SEARCH_DEPTH);
        for (; pos.getY() >= bottom; pos.move(Direction.DOWN)) {
            VoxelShape shape = mob.level().getBlockState(pos).getCollisionShape(mob.level(), pos);
            if (!shape.isEmpty()) {
                return OptionalDouble.of(pos.getY() + shape.max(Direction.Axis.Y));
            }
        }
        return OptionalDouble.empty();
    }
}
