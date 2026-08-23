package com.breakinblocks.neovitae.compat.occultism;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public final class OccultismCompat {

    private static boolean available;

    private OccultismCompat() {
    }

    public static void init() {
        available = ModList.get().isLoaded("occultism");
        if (available) {
            NeoVitae.LOGGER.info("Occultism found, storage actuators can be bound with the Sigil of Bound Treasures");
        }
    }

    public static boolean isStorageAccess(Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return OccultismHooks.isStorageAccess(level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    public static boolean openStorageAccess(ServerPlayer player, Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return OccultismHooks.openStorageAccess(player, level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    private static void disable(Throwable t) {
        available = false;
        NeoVitae.LOGGER.warn("Occultism storage lookup failed, skipping its blocks from now on", t);
    }
}
