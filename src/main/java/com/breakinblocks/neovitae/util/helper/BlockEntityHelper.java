package com.breakinblocks.neovitae.util.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class BlockEntityHelper {
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    public static MutableComponent translatableHover(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent translatableHover(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.GRAY);
    }

    public static void dropContents(Level level, BlockPos pos, ResourceHandler<ItemResource> handler) {
        for (int i = 0; i < handler.size(); i++) {
            ItemResource r = handler.getResource(i);
            if (r.isEmpty()) continue;
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), r.toStack(handler.getAmountAsInt(i)));
        }
    }
}
