package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Sent from client to server when player sneak+left-clicks with a ritual diviner.
 * Used to cycle backwards through rituals.
 */
public record RitualDivinerCyclePayload(boolean reverse) implements CustomPacketPayload {

    public static final Type<RitualDivinerCyclePayload> TYPE =
            new Type<>(NeoVitae.rl("ritual_diviner_cycle"));

    public static final StreamCodec<FriendlyByteBuf, RitualDivinerCyclePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, RitualDivinerCyclePayload::reverse,
                    RitualDivinerCyclePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
