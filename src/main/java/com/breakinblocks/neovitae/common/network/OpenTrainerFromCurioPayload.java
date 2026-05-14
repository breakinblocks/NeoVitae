package com.breakinblocks.neovitae.common.network;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenTrainerFromCurioPayload() implements CustomPacketPayload {

    public static final OpenTrainerFromCurioPayload INSTANCE = new OpenTrainerFromCurioPayload();

    public static final Type<OpenTrainerFromCurioPayload> TYPE =
            new Type<>(NeoVitae.rl("open_trainer_from_curio"));

    public static final StreamCodec<FriendlyByteBuf, OpenTrainerFromCurioPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
