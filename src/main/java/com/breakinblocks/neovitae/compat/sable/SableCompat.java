package com.breakinblocks.neovitae.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public final class SableCompat {

    private static final boolean LOADED = ModList.get().isLoaded("sable");

    private SableCompat() {}

    public record ContraptionView(Level subLevel, BlockPos localCenter) {}

    public static boolean isLoaded() {
        return LOADED;
    }

    public static boolean isOnContraption(Level level, Vec3 pos) {
        if (!LOADED || level == null) return false;
        try {
            return SableCompatImpl.isOnContraption(level, pos);
        } catch (Throwable t) {
            return false;
        }
    }

    public static Vec3 toDisplayPos(Level level, Vec3 pos) {
        if (!LOADED || level == null) return pos;
        try {
            return SableCompatImpl.toDisplayPos(level, pos);
        } catch (Throwable t) {
            return pos;
        }
    }

    public static Vec3 rotateByContraption(Level level, BlockPos atPos, Vec3 vec) {
        if (!LOADED || level == null) return vec;
        try {
            return SableCompatImpl.rotateByContraption(level, atPos, vec);
        } catch (Throwable t) {
            return vec;
        }
    }

    public static ContraptionView viewForEntity(Entity entity) {
        if (!LOADED || entity == null) return null;
        try {
            return SableCompatImpl.viewForEntity(entity);
        } catch (Throwable t) {
            return null;
        }
    }
}
