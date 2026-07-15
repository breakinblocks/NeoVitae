package com.breakinblocks.neovitae.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.neoforge.FTBUltimineEvent;
import net.neoforged.neoforge.common.NeoForge;

final class FTBUltimineHooks {

    private FTBUltimineHooks() {
    }

    static void register() {
        NeoForge.EVENT_BUS.addListener(FTBUltimineEvent.RegisterRestrictionHandler.class,
                event -> event.getEventData().register(player -> !FTBUltimineCompat.isSuppressed()));
    }
}
