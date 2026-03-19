package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Payload sent from client to server to cycle the selected sigil in the Sigil of Holding
 * via scroll wheel (not through the menu GUI).
 *
 * @param slot      The player inventory slot containing the Sigil of Holding
 * @param direction The scroll direction: 1 for forward, -1 for backward
 */
public record SigilHoldingCyclePayload(int slot, int direction) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SigilHoldingCyclePayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("sigil_holding_cycle"));

    public static final StreamCodec<FriendlyByteBuf, SigilHoldingCyclePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SigilHoldingCyclePayload::slot,
                    ByteBufCodecs.INT, SigilHoldingCyclePayload::direction,
                    SigilHoldingCyclePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
