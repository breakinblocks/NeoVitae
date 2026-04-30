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
        double steadfastWill,
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
            buf.writeDouble(payload.corrosiveWill);
            buf.writeDouble(payload.destructiveWill);
            buf.writeDouble(payload.vengefulWill);
            buf.writeDouble(payload.steadfastWill);
            buf.writeDouble(payload.bonusRaw);
            buf.writeDouble(payload.bonusCorrosive);
            buf.writeDouble(payload.bonusDestructive);
            buf.writeDouble(payload.bonusVengeful);
            buf.writeDouble(payload.bonusSteadfast);
        }
    };

    public static SpiritusSyncPayload fromSpiritusChunk(int chunkX, int chunkZ, SpiritusChunk willChunk) {
        return new SpiritusSyncPayload(
                chunkX,
                chunkZ,
                willChunk.getSpiritus(SpiritusType.RAW),
                willChunk.getSpiritus(SpiritusType.RUINA),
                willChunk.getSpiritus(SpiritusType.NIHILUM),
                willChunk.getSpiritus(SpiritusType.VINDICTA),
                willChunk.getSpiritus(SpiritusType.INVICTUS),
                willChunk.getMaxBonus(SpiritusType.RAW),
                willChunk.getMaxBonus(SpiritusType.RUINA),
                willChunk.getMaxBonus(SpiritusType.NIHILUM),
                willChunk.getMaxBonus(SpiritusType.VINDICTA),
                willChunk.getMaxBonus(SpiritusType.INVICTUS)
        );
    }

    public SpiritusChunk toSpiritusChunk() {
        return new SpiritusChunk(
                rawSpiritus, corrosiveWill, destructiveWill, vengefulWill, steadfastWill,
                bonusRaw, bonusCorrosive, bonusDestructive, bonusVengeful, bonusSteadfast
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
