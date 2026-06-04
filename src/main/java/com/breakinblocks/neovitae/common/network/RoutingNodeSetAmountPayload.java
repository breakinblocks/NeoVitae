package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record RoutingNodeSetAmountPayload(BlockPos pos, boolean fluid, int ghostSlot, int amount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RoutingNodeSetAmountPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("routing_node_set_amount"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RoutingNodeSetAmountPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RoutingNodeSetAmountPayload::pos,
                    ByteBufCodecs.BOOL, RoutingNodeSetAmountPayload::fluid,
                    ByteBufCodecs.INT, RoutingNodeSetAmountPayload::ghostSlot,
                    ByteBufCodecs.INT, RoutingNodeSetAmountPayload::amount,
                    RoutingNodeSetAmountPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
