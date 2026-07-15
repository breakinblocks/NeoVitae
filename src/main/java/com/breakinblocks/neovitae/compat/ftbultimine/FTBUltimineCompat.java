package com.breakinblocks.neovitae.compat.ftbultimine;

import com.breakinblocks.neovitae.NeoVitae;
import net.neoforged.fml.ModList;

public final class FTBUltimineCompat {

    private static int depth = 0;

    private FTBUltimineCompat() {
    }

    public static void init() {
        if (!ModList.get().isLoaded("ftbultimine")) {
            return;
        }
        try {
            FTBUltimineHooks.register();
            NeoVitae.LOGGER.info("Armed FTB Ultimine suppression for ritual block operations");
        } catch (Throwable t) {
            NeoVitae.LOGGER.warn("Failed to arm FTB Ultimine ritual suppression", t);
        }
    }

    public static boolean isSuppressed() {
        return depth > 0;
    }

    public static void beginSuppress() {
        depth++;
    }

    public static void endSuppress() {
        if (depth > 0) {
            depth--;
        }
    }
}
