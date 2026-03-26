package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.SpiritusChunk;

/**
 * Payload for syncing spiritus aura data from server to client.
 * Sent when a player enters a chunk or when will amounts change.
 */
public record SpiritusSyncPayload(
        int chunkX,
        int chunkZ,
        double rawSpiritus,
        double corrosiveWill,
        double destructiveWill,
        double vengefulWill,
        double steadfastWill
) implements CustomPacketPayload {

    public static final Type<SpiritusSyncPayload> TYPE = new Type<>(NeoVitae.rl("will_chunk_sync"));

    public static final StreamCodec<FriendlyByteBuf, SpiritusSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SpiritusSyncPayload decode(FriendlyByteBuf buf) {
            return new SpiritusSyncPayload(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
            );
        }

        @Override
        public void encode(FriendlyByteBuf buf, SpiritusSyncPayload payload) {
            buf.writeInt(payload.chunkX);
            buf.writeInt(payload.chunkZ);
            buf.writeDouble(payload.rawSpiritus);
            buf.writeDouble(payload.corrosiveWill);
            buf.writeDouble(payload.destructiveWill);
            buf.writeDouble(payload.vengefulWill);
            buf.writeDouble(payload.steadfastWill);
        }
    };

    public static SpiritusSyncPayload fromSpiritusChunk(int chunkX, int chunkZ, SpiritusChunk willChunk) {
        return new SpiritusSyncPayload(
                chunkX,
                chunkZ,
                willChunk.getWill(SpiritusType.DEFAULT),
                willChunk.getWill(SpiritusType.CORROSIVE),
                willChunk.getWill(SpiritusType.DESTRUCTIVE),
                willChunk.getWill(SpiritusType.VENGEFUL),
                willChunk.getWill(SpiritusType.STEADFAST)
        );
    }

    public SpiritusChunk toSpiritusChunk() {
        return new SpiritusChunk(rawSpiritus, corrosiveWill, destructiveWill, vengefulWill, steadfastWill);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
