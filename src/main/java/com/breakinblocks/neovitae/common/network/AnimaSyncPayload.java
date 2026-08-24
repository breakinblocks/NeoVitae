package com.breakinblocks.neovitae.common.network;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record AnimaSyncPayload(UUID owner, int currentEV) implements CustomPacketPayload {

    public static final Type<AnimaSyncPayload> TYPE = new Type<>(NeoVitae.rl("anima_sync"));

    public static final StreamCodec<FriendlyByteBuf, AnimaSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AnimaSyncPayload decode(FriendlyByteBuf buf) {
            return new AnimaSyncPayload(buf.readUUID(), buf.readVarInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, AnimaSyncPayload payload) {
            buf.writeUUID(payload.owner);
            buf.writeVarInt(payload.currentEV);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
