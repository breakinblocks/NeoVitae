package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record PasteSideConfigPayload(BlockPos pos, byte[] mask, int face) implements CustomPacketPayload {

    public static final Type<PasteSideConfigPayload> TYPE =
            new Type<>(NeoVitae.rl("paste_side_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PasteSideConfigPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, PasteSideConfigPayload::pos,
                    ByteBufCodecs.byteArray(256), PasteSideConfigPayload::mask,
                    ByteBufCodecs.VAR_INT, PasteSideConfigPayload::face,
                    PasteSideConfigPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
