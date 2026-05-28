package com.breakinblocks.neovitae.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;

/**
 * Payload for syncing spiritus aura data from server to client.
 * Sent when a player enters a chunk or when will amounts change.
 */
public record SpiritusSyncPayload(
        int chunkX,
        int chunkZ,
        double rawSpiritus,
        double corrosiveSpiritus,
        double destructiveSpiritus,
        double vengefulSpiritus,
        double steadfastSpiritus,
        double bonusRaw,
        double bonusCorrosive,
        double bonusDestructive,
        double bonusVengeful,
        double bonusSteadfast
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
                    buf.readDouble(),
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
            buf.writeDouble(payload.corrosiveSpiritus);
            buf.writeDouble(payload.destructiveSpiritus);
            buf.writeDouble(payload.vengefulSpiritus);
            buf.writeDouble(payload.steadfastSpiritus);
            buf.writeDouble(payload.bonusRaw);
            buf.writeDouble(payload.bonusCorrosive);
            buf.writeDouble(payload.bonusDestructive);
            buf.writeDouble(payload.bonusVengeful);
            buf.writeDouble(payload.bonusSteadfast);
        }
    };

    public static SpiritusSyncPayload fromSpiritusChunk(int chunkX, int chunkZ, SpiritusChunk spiritusChunkVar) {
        return new SpiritusSyncPayload(
                chunkX,
                chunkZ,
                spiritusChunkVar.getSpiritus(SpiritusType.RAW),
                spiritusChunkVar.getSpiritus(SpiritusType.RUINA),
                spiritusChunkVar.getSpiritus(SpiritusType.NIHILUM),
                spiritusChunkVar.getSpiritus(SpiritusType.VINDICTA),
                spiritusChunkVar.getSpiritus(SpiritusType.INVICTUS),
                spiritusChunkVar.getMaxBonus(SpiritusType.RAW),
                spiritusChunkVar.getMaxBonus(SpiritusType.RUINA),
                spiritusChunkVar.getMaxBonus(SpiritusType.NIHILUM),
                spiritusChunkVar.getMaxBonus(SpiritusType.VINDICTA),
                spiritusChunkVar.getMaxBonus(SpiritusType.INVICTUS)
        );
    }

    public SpiritusChunk toSpiritusChunk() {
        return new SpiritusChunk(
                rawSpiritus, corrosiveSpiritus, destructiveSpiritus, vengefulSpiritus, steadfastSpiritus,
                bonusRaw, bonusCorrosive, bonusDestructive, bonusVengeful, bonusSteadfast
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
