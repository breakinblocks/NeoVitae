package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Payload for updating ghost item stacks in filter menus.
 */
public record FilterGhostSlotPayload(int ghostSlot, ItemStack stack) implements CustomPacketPayload {
    public static final Type<FilterGhostSlotPayload> TYPE = new Type<>(NeoVitae.rl("filter_ghost_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterGhostSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FilterGhostSlotPayload::ghostSlot,
            ItemStack.OPTIONAL_STREAM_CODEC, FilterGhostSlotPayload::stack,
            FilterGhostSlotPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
