package com.breakinblocks.neovitae.compat.occultism;

import com.klikli_dev.occultism.api.common.blockentity.IStorageController;
import com.klikli_dev.occultism.api.common.blockentity.IStorageControllerProxy;
import com.klikli_dev.occultism.common.container.storage.StorageControllerContainerBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

final class OccultismHooks {

    private OccultismHooks() {
    }

    static boolean isStorageAccess(Level level, BlockPos pos) {
        return toStorageAccess(level.getBlockEntity(pos)) != null;
    }

    static boolean openStorageAccess(ServerPlayer player, Level level, BlockPos pos) {
        MenuProvider provider = toStorageAccess(level.getBlockEntity(pos));
        if (provider == null) {
            return false;
        }
        if (!StorageControllerContainerBase.canOpen(player, pos)) {
            return true;
        }
        player.openMenu(provider, pos);
        StorageControllerContainerBase.reserve(player, pos);
        return true;
    }

    private static MenuProvider toStorageAccess(BlockEntity be) {
        if (!(be instanceof MenuProvider provider)) {
            return null;
        }
        return be instanceof IStorageController || be instanceof IStorageControllerProxy ? provider : null;
    }
}
