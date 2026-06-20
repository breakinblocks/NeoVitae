package com.breakinblocks.neovitae.common.network;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record LexModeCyclePayload() implements CustomPacketPayload {

    public static final LexModeCyclePayload INSTANCE = new LexModeCyclePayload();

    public static final Type<LexModeCyclePayload> TYPE =
            new Type<>(NeoVitae.rl("lex_mode_cycle"));

    public static final StreamCodec<FriendlyByteBuf, LexModeCyclePayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
