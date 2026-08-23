package com.breakinblocks.neovitae.compat.refinedstorage;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public final class RefinedStorageCompat {

    private static boolean available;

    private RefinedStorageCompat() {
    }

    public static void init() {
        available = ModList.get().isLoaded("refinedstorage");
        if (available) {
            NeoVitae.LOGGER.info("Refined Storage found, grids can be bound with the Sigil of Bound Treasures");
        }
    }

    public static boolean hasExtendedMenu(Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return RefinedStorageHooks.hasExtendedMenu(level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    public static boolean openExtendedMenu(ServerPlayer player, Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return RefinedStorageHooks.openExtendedMenu(player, level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    private static void disable(Throwable t) {
        available = false;
        NeoVitae.LOGGER.warn("Refined Storage menu lookup failed, skipping its blocks from now on", t);
    }
}
