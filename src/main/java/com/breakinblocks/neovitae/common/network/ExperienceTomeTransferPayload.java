package com.breakinblocks.neovitae.common.network;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Moves experience between the player and an open Tome of Perditia.
 * A negative level count means "as much as will move".
 */
public record ExperienceTomeTransferPayload(boolean deposit, int levels) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExperienceTomeTransferPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("experience_tome_transfer"));

    public static final StreamCodec<FriendlyByteBuf, ExperienceTomeTransferPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ExperienceTomeTransferPayload::deposit,
                    ByteBufCodecs.INT, ExperienceTomeTransferPayload::levels,
                    ExperienceTomeTransferPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
