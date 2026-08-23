package com.breakinblocks.neovitae.compat.arsnouveau;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public final class ArsNouveauCompat {

    private static boolean available;

    private ArsNouveauCompat() {
    }

    public static void init() {
        available = ModList.get().isLoaded("ars_nouveau");
        if (available) {
            NeoVitae.LOGGER.info("Ars Nouveau found, storage lecterns can be bound with the Sigil of Bound Treasures");
        }
    }

    public static boolean isStorageLectern(Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return ArsNouveauHooks.isStorageLectern(level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    public static boolean openStorageLectern(ServerPlayer player, Level level, BlockPos pos) {
        if (!available) {
            return false;
        }
        try {
            return ArsNouveauHooks.openStorageLectern(player, level, pos);
        } catch (Throwable t) {
            disable(t);
            return false;
        }
    }

    private static void disable(Throwable t) {
        available = false;
        NeoVitae.LOGGER.warn("Ars Nouveau storage lectern lookup failed, skipping its blocks from now on", t);
    }
}
