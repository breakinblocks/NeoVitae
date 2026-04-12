package com.breakinblocks.neovitae.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.NeoVitae;

public record RoutingNodeSetFluidGhostPayload(BlockPos pos, int ghostSlot, FluidStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RoutingNodeSetFluidGhostPayload> TYPE =
            new CustomPacketPayload.Type<>(NeoVitae.rl("routing_node_set_fluid_ghost"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RoutingNodeSetFluidGhostPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RoutingNodeSetFluidGhostPayload::pos,
                    ByteBufCodecs.INT, RoutingNodeSetFluidGhostPayload::ghostSlot,
                    FluidStack.OPTIONAL_STREAM_CODEC, RoutingNodeSetFluidGhostPayload::stack,
                    RoutingNodeSetFluidGhostPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
