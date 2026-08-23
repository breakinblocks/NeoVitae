package com.breakinblocks.neovitae.compat.arsnouveau;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

final class ArsNouveauHooks {

    private ArsNouveauHooks() {
    }

    static boolean isStorageLectern(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof StorageLecternTile;
    }

    static boolean openStorageLectern(ServerPlayer player, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof StorageLecternTile lectern && lectern.openMenu(player);
    }
}
