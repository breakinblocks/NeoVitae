package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.NeoVitae;

public record SetTrainingCapPayload(ResourceLocation upgrade, int level) implements CustomPacketPayload {

    public static final Type<SetTrainingCapPayload> TYPE = new Type<>(NeoVitae.rl("set_training_cap"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetTrainingCapPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, SetTrainingCapPayload::upgrade,
            ByteBufCodecs.VAR_INT, SetTrainingCapPayload::level,
            SetTrainingCapPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
