package com.breakinblocks.neovitae.compat.ftbultimine;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public final class FTBUltimineCompat {

    private FTBUltimineCompat() {
    }

    public static void init() {
        if (!ModList.get().isLoaded("ftbultimine")) {
            return;
        }
        try {
            Class<?> ultimine = Class.forName("dev.ftb.mods.ftbultimine.FTBUltimine");
            Predicate<Player> previous = capturePrevious(ultimine);
            Predicate<Player> override = player ->
                    !BlockProtectionHelper.isRitualActionInProgress()
                            && (previous == null || previous.test(player));
            ultimine.getMethod("setPermissionOverride", Predicate.class).invoke(null, override);
            NeoVitae.LOGGER.info("Installed FTB Ultimine suppression for ritual block operations");
        } catch (Throwable t) {
            NeoVitae.LOGGER.warn("Failed to install FTB Ultimine ritual suppression", t);
        }
    }

    @SuppressWarnings("unchecked")
    private static Predicate<Player> capturePrevious(Class<?> ultimine) {
        try {
            Field field = ultimine.getDeclaredField("permissionOverride");
            field.setAccessible(true);
            return (Predicate<Player>) field.get(null);
        } catch (Throwable t) {
            return null;
        }
    }
}
