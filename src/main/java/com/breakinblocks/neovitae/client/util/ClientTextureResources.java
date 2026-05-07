package com.breakinblocks.neovitae.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public final class ClientTextureResources {
    private ClientTextureResources() {}

    public static ResourceManager get() {
        return Minecraft.getInstance().getResourceManager();
    }
}
