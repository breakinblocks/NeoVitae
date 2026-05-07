package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record LexCycleRadiusPayload(int direction) implements CustomPacketPayload {

    public static final Type<LexCycleRadiusPayload> TYPE = new Type<>(NeoVitae.rl("lex_cycle_radius"));

    public static final StreamCodec<FriendlyByteBuf, LexCycleRadiusPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, LexCycleRadiusPayload::direction,
                    LexCycleRadiusPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
