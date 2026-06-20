package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record ShieldKeyPayload(boolean active) implements CustomPacketPayload {

    public static final Type<ShieldKeyPayload> TYPE =
            new Type<>(NeoVitae.rl("shield_key"));

    public static final StreamCodec<FriendlyByteBuf, ShieldKeyPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ShieldKeyPayload::active,
                    ShieldKeyPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
