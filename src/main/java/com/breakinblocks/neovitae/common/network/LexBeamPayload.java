package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record LexBeamPayload(boolean firing) implements CustomPacketPayload {

    public static final Type<LexBeamPayload> TYPE =
            new Type<>(NeoVitae.rl("lex_beam"));

    public static final StreamCodec<FriendlyByteBuf, LexBeamPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, LexBeamPayload::firing,
                    LexBeamPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
