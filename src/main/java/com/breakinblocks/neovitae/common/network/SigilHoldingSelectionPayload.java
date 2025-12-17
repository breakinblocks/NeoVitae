package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Payload sent from client to server to update the selected sigil slot in the Sigil of Holding.
 */
public record SigilHoldingSelectionPayload(int selectedSlot) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SigilHoldingSelectionPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("sigil_holding_selection"));

    public static final StreamCodec<FriendlyByteBuf, SigilHoldingSelectionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SigilHoldingSelectionPayload::selectedSlot,
                    SigilHoldingSelectionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
