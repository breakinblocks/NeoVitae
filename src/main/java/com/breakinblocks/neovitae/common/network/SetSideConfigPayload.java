package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record SetSideConfigPayload(BlockPos pos, int slot, int direction, boolean enabled) implements CustomPacketPayload {

    public static final Type<SetSideConfigPayload> TYPE =
            new Type<>(NeoVitae.rl("set_side_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetSideConfigPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetSideConfigPayload::pos,
                    ByteBufCodecs.VAR_INT, SetSideConfigPayload::slot,
                    ByteBufCodecs.VAR_INT, SetSideConfigPayload::direction,
                    ByteBufCodecs.BOOL, SetSideConfigPayload::enabled,
                    SetSideConfigPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
