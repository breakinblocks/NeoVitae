package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.StreamEffect;

public record SiphonStreamPayload(boolean fromOrb, StreamEffect effect) implements CustomPacketPayload {

    public static final Type<SiphonStreamPayload> TYPE = new Type<>(NeoVitae.rl("siphon_stream_fx"));

    public static final StreamCodec<FriendlyByteBuf, SiphonStreamPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SiphonStreamPayload decode(FriendlyByteBuf buf) {
            boolean fromOrb = buf.readBoolean();
            return new SiphonStreamPayload(fromOrb, StreamEffect.decode(buf));
        }

        @Override
        public void encode(FriendlyByteBuf buf, SiphonStreamPayload payload) {
            buf.writeBoolean(payload.fromOrb());
            payload.effect().encode(buf);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
