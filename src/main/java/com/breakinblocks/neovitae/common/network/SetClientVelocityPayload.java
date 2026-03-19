package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Payload sent from server to client to set the player's velocity.
 * Required for server-authoritative velocity changes (e.g. RitualSpeed).
 */
public record SetClientVelocityPayload(
        double motionX,
        double motionY,
        double motionZ
) implements CustomPacketPayload {

    public static final Type<SetClientVelocityPayload> TYPE = new Type<>(NeoVitae.rl("set_client_velocity"));

    public static final StreamCodec<FriendlyByteBuf, SetClientVelocityPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SetClientVelocityPayload decode(FriendlyByteBuf buf) {
            return new SetClientVelocityPayload(buf.readDouble(), buf.readDouble(), buf.readDouble());
        }

        @Override
        public void encode(FriendlyByteBuf buf, SetClientVelocityPayload payload) {
            buf.writeDouble(payload.motionX);
            buf.writeDouble(payload.motionY);
            buf.writeDouble(payload.motionZ);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
