package com.breakinblocks.neovitae.compat.refinedstorage;

import com.refinedmods.refinedstorage.common.support.containermenu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

final class RefinedStorageHooks {

    private RefinedStorageHooks() {
    }

    static boolean hasExtendedMenu(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ExtendedMenuProvider<?>;
    }

    static boolean openExtendedMenu(ServerPlayer player, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ExtendedMenuProvider<?> provider)) {
            return false;
        }
        return open(player, provider);
    }

    private static <T> boolean open(ServerPlayer player, ExtendedMenuProvider<T> provider) {
        T data = provider.getMenuData();
        StreamEncoder<RegistryFriendlyByteBuf, T> codec = provider.getMenuCodec();
        return player.openMenu(provider, buf -> codec.encode(buf, data)).isPresent();
    }
}
