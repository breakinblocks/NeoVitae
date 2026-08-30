package com.breakinblocks.neovitae.client.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public final class ClientPlayerAccess {

    private ClientPlayerAccess() {}

    @Nullable
    public static Player currentPlayer() {
        Minecraft mc = Minecraft.getInstance();
        return mc != null ? mc.player : null;
    }
}
