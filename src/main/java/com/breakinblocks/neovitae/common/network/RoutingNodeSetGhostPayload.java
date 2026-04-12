package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;

public record RoutingNodeSetGhostPayload(BlockPos pos, int ghostSlot, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RoutingNodeSetGhostPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("routing_node_set_ghost"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RoutingNodeSetGhostPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RoutingNodeSetGhostPayload::pos,
                    ByteBufCodecs.INT, RoutingNodeSetGhostPayload::ghostSlot,
                    ItemStack.OPTIONAL_STREAM_CODEC, RoutingNodeSetGhostPayload::stack,
                    RoutingNodeSetGhostPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
