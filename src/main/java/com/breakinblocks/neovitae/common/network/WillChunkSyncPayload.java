package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.WillChunk;

/**
 * Payload for syncing demon will aura data from server to client.
 * Sent when a player enters a chunk or when will amounts change.
 */
public record WillChunkSyncPayload(
        int chunkX,
        int chunkZ,
        double rawWill,
        double corrosiveWill,
        double destructiveWill,
        double vengefulWill,
        double steadfastWill
) implements CustomPacketPayload {

    public static final Type<WillChunkSyncPayload> TYPE = new Type<>(NeoVitae.rl("will_chunk_sync"));

    public static final StreamCodec<FriendlyByteBuf, WillChunkSyncPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public WillChunkSyncPayload decode(FriendlyByteBuf buf) {
            return new WillChunkSyncPayload(
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
        public void encode(FriendlyByteBuf buf, WillChunkSyncPayload payload) {
            buf.writeInt(payload.chunkX);
            buf.writeInt(payload.chunkZ);
            buf.writeDouble(payload.rawWill);
            buf.writeDouble(payload.corrosiveWill);
            buf.writeDouble(payload.destructiveWill);
            buf.writeDouble(payload.vengefulWill);
            buf.writeDouble(payload.steadfastWill);
        }
    };

    public static WillChunkSyncPayload fromWillChunk(int chunkX, int chunkZ, WillChunk willChunk) {
        return new WillChunkSyncPayload(
                chunkX,
                chunkZ,
                willChunk.getWill(EnumWillType.DEFAULT),
                willChunk.getWill(EnumWillType.CORROSIVE),
                willChunk.getWill(EnumWillType.DESTRUCTIVE),
                willChunk.getWill(EnumWillType.VENGEFUL),
                willChunk.getWill(EnumWillType.STEADFAST)
        );
    }

    public WillChunk toWillChunk() {
        return new WillChunk(rawWill, corrosiveWill, destructiveWill, vengefulWill, steadfastWill);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
