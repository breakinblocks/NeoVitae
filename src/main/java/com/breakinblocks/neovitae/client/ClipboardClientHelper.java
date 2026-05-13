package com.breakinblocks.neovitae.client;

import net.minecraft.client.Minecraft;

public final class ClipboardClientHelper {

    private ClipboardClientHelper() {
    }

    public static void setClipboard(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
    }
}
