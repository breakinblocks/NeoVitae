package com.breakinblocks.neovitae.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;

final class SableCompatImpl {

    private SableCompatImpl() {}

    static boolean isOnContraption(Level level, Vec3 pos) {
        return SableCompanion.INSTANCE.getContaining(level, pos.x, pos.z) != null;
    }

    static Vec3 toDisplayPos(Level level, Vec3 pos) {
        SubLevelAccess sub = SableCompanion.INSTANCE.getContaining(level, pos.x, pos.z);
        if (sub == null) return pos;
        Pose3dc pose = sub.logicalPose();
        if (pose == null) return pos;
        return pose.transformPosition(pos);
    }

    static Vec3 rotateByContraption(Level level, BlockPos atPos, Vec3 vec) {
        SubLevelAccess sub = SableCompanion.INSTANCE.getContaining(level, atPos.getX(), atPos.getZ());
        if (sub == null) return vec;
        Pose3dc pose = sub.logicalPose();
        if (pose == null) return vec;
        return pose.transformNormal(vec);
    }

    static SableCompat.ContraptionView viewForEntity(Entity entity) {
        SubLevel sub = Sable.HELPER.getTrackingSubLevel(entity);
        if (sub == null) {
            sub = Sable.HELPER.getContaining(entity);
        }
        if (sub == null) return null;
        Level subLevel = sub.getLevel();
        if (subLevel == null) return null;
        Pose3dc pose = sub.logicalPose();
        Vec3 worldVec = entity.position();
        Vec3 localVec = pose == null ? worldVec : pose.transformPositionInverse(worldVec);
        return new SableCompat.ContraptionView(subLevel, BlockPos.containing(localVec));
    }
}
