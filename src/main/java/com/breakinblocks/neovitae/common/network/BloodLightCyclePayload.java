package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record BloodLightCyclePayload(boolean reverse) implements CustomPacketPayload {

    public static final Type<BloodLightCyclePayload> TYPE =
            new Type<>(NeoVitae.rl("blood_light_cycle"));

    public static final StreamCodec<FriendlyByteBuf, BloodLightCyclePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BloodLightCyclePayload::reverse,
                    BloodLightCyclePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
