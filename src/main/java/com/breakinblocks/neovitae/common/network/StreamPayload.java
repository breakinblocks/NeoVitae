package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Server-to-client packet to create a visual energy stream.
 * Source uses doubles for precise entity positioning (e.g. upper chest height).
 * Target uses BlockPos since it's always a block (altar).
 */
public record StreamPayload(
        double sourceX,
        double sourceY,
        double sourceZ,
        BlockPos target,
        int color
) implements CustomPacketPayload {

    public static final Type<StreamPayload> TYPE = new Type<>(NeoVitae.rl("stream_fx"));

    public static final StreamCodec<FriendlyByteBuf, StreamPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StreamPayload decode(FriendlyByteBuf buf) {
            return new StreamPayload(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readBlockPos(), buf.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, StreamPayload payload) {
            buf.writeDouble(payload.sourceX);
            buf.writeDouble(payload.sourceY);
            buf.writeDouble(payload.sourceZ);
            buf.writeBlockPos(payload.target);
            buf.writeInt(payload.color);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
