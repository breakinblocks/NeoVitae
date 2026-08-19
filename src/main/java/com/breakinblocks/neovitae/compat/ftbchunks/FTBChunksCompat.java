package com.breakinblocks.neovitae.compat.ftbchunks;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class FTBChunksCompat {

    private static boolean available;

    private FTBChunksCompat() {
    }

    public static void init() {
        available = ModList.get().isLoaded("ftbchunks");
        if (available) {
            NeoVitae.LOGGER.info("FTB Chunks found, claim permissions will be checked before block edits");
        }
    }

    public static boolean canEditBlock(Player player, BlockPos pos) {
        if (!available) {
            return true;
        }

        try {
            return FTBChunksHooks.canEditBlock(player, pos);
        } catch (Throwable t) {
            available = false;
            NeoVitae.LOGGER.warn("FTB Chunks claim lookup failed, skipping claim checks from now on", t);
            return true;
        }
    }
}
