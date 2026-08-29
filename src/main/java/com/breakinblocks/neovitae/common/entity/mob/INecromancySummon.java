package com.breakinblocks.neovitae.common.entity.mob;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface INecromancySummon {

    void setOwner(Player owner);

    @Nullable
    UUID getOwnerUUID();
}
