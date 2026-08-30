package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record AlternatorConfigPayload(BlockPos pos, int delay, boolean stopOnRedstone) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AlternatorConfigPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("alternator_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlternatorConfigPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, AlternatorConfigPayload::pos,
                    ByteBufCodecs.INT, AlternatorConfigPayload::delay,
                    ByteBufCodecs.BOOL, AlternatorConfigPayload::stopOnRedstone,
                    AlternatorConfigPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
