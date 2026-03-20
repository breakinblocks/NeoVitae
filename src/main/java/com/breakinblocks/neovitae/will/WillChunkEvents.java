package com.breakinblocks.neovitae.will;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class WillChunkEvents {

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerPlayer player = event.getPlayer();
        ServerLevel level = (ServerLevel) event.getLevel();
        ChunkPos chunkPos = event.getPos();

        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);
        WillChunk willChunk = chunk.getData(NVDataAttachments.WILL_CHUNK);

        if (willChunk.hasWill()) {
            WorldDemonWillHandler.syncChunkToPlayer(player, chunkPos, willChunk);
        }
    }
}
