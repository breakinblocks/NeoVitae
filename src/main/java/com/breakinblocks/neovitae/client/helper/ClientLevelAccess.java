package com.breakinblocks.neovitae.client.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ClientLevelAccess {

    private ClientLevelAccess() {}

    @Nullable
    public static Level currentLevel() {
        Minecraft mc = Minecraft.getInstance();
        return mc != null ? mc.level : null;
    }
}
