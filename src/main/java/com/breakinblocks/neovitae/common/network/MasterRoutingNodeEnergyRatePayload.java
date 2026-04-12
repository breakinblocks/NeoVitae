package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

public record MasterRoutingNodeEnergyRatePayload(BlockPos pos, int rate) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MasterRoutingNodeEnergyRatePayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("master_routing_node_energy_rate"));

    public static final StreamCodec<FriendlyByteBuf, MasterRoutingNodeEnergyRatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, MasterRoutingNodeEnergyRatePayload::pos,
                    ByteBufCodecs.INT, MasterRoutingNodeEnergyRatePayload::rate,
                    MasterRoutingNodeEnergyRatePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
