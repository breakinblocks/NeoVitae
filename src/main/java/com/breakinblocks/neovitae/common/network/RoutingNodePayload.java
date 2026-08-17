package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * {@code value} is action-specific: direction index (SELECT/SWAP), ghost slot index
 * (CLEAR_*), or ignored (priority/toggle actions operate on the current slot).
 */
public record RoutingNodePayload(BlockPos pos, int action, int value) implements CustomPacketPayload {

    public static final int ACTION_SELECT_SLOT = 0;
    public static final int ACTION_INCREMENT_PRIORITY = 1;
    public static final int ACTION_DECREMENT_PRIORITY = 2;
    public static final int ACTION_SWAP_PRIORITY = 3;
    public static final int ACTION_TOGGLE_SIDE_ENABLED = 4;
    public static final int ACTION_TOGGLE_SIDE_ITEM_MODE = 5;
    public static final int ACTION_CLEAR_ITEM_GHOST = 6;
    public static final int ACTION_TOGGLE_SIDE_FLUID_MODE = 7;
    public static final int ACTION_CLEAR_FLUID_GHOST = 8;
    public static final int ACTION_CYCLE_SPIRITUS_TYPE = 9;
    public static final int ACTION_ADJUST_SPIRITUS_STOCK = 10;
    public static final int ACTION_TOGGLE_SIDE_ENERGY = 11;
    public static final int ACTION_CYCLE_SIDE_DIRECTION = 12;
    public static final int ACTION_SET_SIDE_ENERGY_RATE = 13;

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
