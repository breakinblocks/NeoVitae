package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.List;

public record RoutingNodeSetComponentsPayload(BlockPos pos, int ghostSlot, List<Identifier> components) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RoutingNodeSetComponentsPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("routing_node_set_components"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RoutingNodeSetComponentsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RoutingNodeSetComponentsPayload::pos,
                    ByteBufCodecs.INT, RoutingNodeSetComponentsPayload::ghostSlot,
                    Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), RoutingNodeSetComponentsPayload::components,
                    RoutingNodeSetComponentsPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
