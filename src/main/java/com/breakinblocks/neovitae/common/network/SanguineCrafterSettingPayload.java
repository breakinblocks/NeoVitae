package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record SanguineCrafterSettingPayload(BlockPos pos, int setting, int value) implements CustomPacketPayload {

    public static final int MODE = 0;
    public static final int REDSTONE = 1;
    public static final int LIMIT = 2;
    public static final int CHUNK_LOADING = 3;

    public static final Type<SanguineCrafterSettingPayload> TYPE =
            new Type<>(NeoVitae.rl("sanguine_crafter_setting"));

    public static final StreamCodec<FriendlyByteBuf, SanguineCrafterSettingPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SanguineCrafterSettingPayload::pos,
                    ByteBufCodecs.VAR_INT, SanguineCrafterSettingPayload::setting,
                    ByteBufCodecs.VAR_INT, SanguineCrafterSettingPayload::value,
                    SanguineCrafterSettingPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
