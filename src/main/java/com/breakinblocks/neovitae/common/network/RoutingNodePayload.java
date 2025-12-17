package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Payload sent from client to server to update routing node settings.
 */
public record RoutingNodePayload(BlockPos pos, int action, int value) implements CustomPacketPayload {

    // Action constants
    public static final int ACTION_SELECT_SLOT = 0;
    public static final int ACTION_INCREMENT_PRIORITY = 1;
    public static final int ACTION_DECREMENT_PRIORITY = 2;
    public static final int ACTION_SWAP_PRIORITY = 3; // value = direction to swap with current

    public static final CustomPacketPayload.Type<RoutingNodePayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("routing_node"));

    public static final StreamCodec<FriendlyByteBuf, RoutingNodePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RoutingNodePayload::pos,
                    ByteBufCodecs.INT, RoutingNodePayload::action,
                    ByteBufCodecs.INT, RoutingNodePayload::value,
                    RoutingNodePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
