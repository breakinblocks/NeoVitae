package com.breakinblocks.neovitae.compat.ftbultimine;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public final class FTBUltimineCompat {

    private static final Predicate<Player> DENY = player -> false;

    private static boolean active = false;
    private static Method setPermissionOverride;
    private static Field permissionOverrideField;

    private static int depth = 0;
    private static Object savedOverride;

    private FTBUltimineCompat() {
    }

    public static void init() {
        if (!ModList.get().isLoaded("ftbultimine")) {
            return;
        }
        try {
            Class<?> ultimine = Class.forName("dev.ftb.mods.ftbultimine.FTBUltimine");
            setPermissionOverride = ultimine.getMethod("setPermissionOverride", Predicate.class);
            permissionOverrideField = ultimine.getDeclaredField("permissionOverride");
            permissionOverrideField.setAccessible(true);
            active = true;
            NeoVitae.LOGGER.info("Armed FTB Ultimine suppression for ritual block operations");
        } catch (Throwable t) {
            NeoVitae.LOGGER.warn("Failed to arm FTB Ultimine ritual suppression", t);
        }
    }

    public static void beginSuppress() {
        if (!active) {
            return;
        }
        if (depth++ == 0) {
            try {
                savedOverride = permissionOverrideField.get(null);
                setPermissionOverride.invoke(null, DENY);
            } catch (Throwable t) {
                savedOverride = null;
            }
        }
    }

    public static void endSuppress() {
        if (!active) {
            return;
        }
        if (--depth == 0 && savedOverride != null) {
            try {
                setPermissionOverride.invoke(null, savedOverride);
            } catch (Throwable ignored) {
            } finally {
                savedOverride = null;
            }
        }
    }
}
