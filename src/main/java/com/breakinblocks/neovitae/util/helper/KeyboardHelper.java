package com.breakinblocks.neovitae.util.helper;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.lwjgl.glfw.GLFW;

/**
 * Modifier-key queries outside an active GUI context. Replaces
 * Screen.hasShiftDown / hasAltDown / hasControlDown, which were removed
 * in 26.1 in favor of per-event modifier flags on KeyEvent / MouseButtonEvent.
 *
 * <p>Safe to call from code that may execute on the dedicated server: all
 * methods return {@code false} when not on the client.
 */
public final class KeyboardHelper {
    private KeyboardHelper() {}

    private static boolean isKeyDown(int keyCode) {
        var loader = FMLLoader.getCurrentOrNull();
        if (loader == null || loader.getDist() != Dist.CLIENT) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) return false;
        return InputConstants.isKeyDown(mc.getWindow(), keyCode);
    }

    public static boolean isShiftDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isAltDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean isControlDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }
}
