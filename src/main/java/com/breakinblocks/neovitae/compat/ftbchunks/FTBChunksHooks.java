package com.breakinblocks.neovitae.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbchunks.api.Protection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

final class FTBChunksHooks {

    private FTBChunksHooks() {
    }

    static boolean canEditBlock(Player player, BlockPos pos) {
        if (!(player instanceof ServerPlayer serverPlayer) || !FTBChunksAPI.api().isManagerLoaded()) {
            return true;
        }

        return !FTBChunksAPI.api().getManager()
                .shouldPreventInteraction(serverPlayer, InteractionHand.MAIN_HAND, pos, Protection.EDIT_BLOCK, null);
    }
}
