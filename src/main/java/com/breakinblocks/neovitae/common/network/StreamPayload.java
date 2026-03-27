package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.StreamEffect;

/**
 * Server-to-client packet carrying a full {@link StreamEffect} configuration.
 */
public record StreamPayload(StreamEffect effect) implements CustomPacketPayload {

    public static final Type<StreamPayload> TYPE = new Type<>(NeoVitae.rl("stream_fx"));

    public static final StreamCodec<FriendlyByteBuf, StreamPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StreamPayload decode(FriendlyByteBuf buf) {
            return new StreamPayload(StreamEffect.decode(buf));
        }

        @Override
        public void encode(FriendlyByteBuf buf, StreamPayload payload) {
            payload.effect.encode(buf);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
