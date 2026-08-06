package com.breakinblocks.neovitae.compat.enderio;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;

public final class EnderIOCompat {

    private static final boolean LOADED = ModList.get().isLoaded("enderio");

    private EnderIOCompat() {}

    public record PoweredSpawnerSnapshot(EntityType<?> entityType, double mobsPerTick) {}

    public static boolean isLoaded() {
        return LOADED;
    }

    public static boolean isPoweredSpawner(BlockEntity be) {
        if (!LOADED || be == null) return false;
        try {
            return EnderIOCompatImpl.isPoweredSpawner(be);
        } catch (Throwable t) {
            return false;
        }
    }

    public static PoweredSpawnerSnapshot readPoweredSpawner(BlockEntity be) {
        if (!LOADED || be == null) return null;
        try {
            return EnderIOCompatImpl.readPoweredSpawner(be);
        } catch (Throwable t) {
            return null;
        }
    }
}
