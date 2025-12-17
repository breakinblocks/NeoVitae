package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.common.world.BMSavedData;
import com.breakinblocks.neovitae.common.world.DungeonSavedData;

import javax.annotation.Nullable;
import java.util.UUID;


@EventBusSubscriber
public class SoulNetworkHelper {
    @Nullable
    private static BMSavedData SD_INSTANCE;

    @Nullable
    private static DungeonSavedData DUNGEON_SD_INSTANCE;

    @SubscribeEvent
    public static void resetSavedDataInstance(ServerStoppedEvent event) {
        SD_INSTANCE = null;
        DUNGEON_SD_INSTANCE = null;
    }

    private static BMSavedData getSavedData() {
        if (SD_INSTANCE == null) {
            if (ServerLifecycleHooks.getCurrentServer() == null)
                return null;

            DimensionDataStorage dimData = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
            SD_INSTANCE = dimData.computeIfAbsent(new Factory<>(BMSavedData::new, BMSavedData::load), BMSavedData.ID);
        }
        return SD_INSTANCE;
    }

    private static DungeonSavedData getDungeonSavedData() {
        if (DUNGEON_SD_INSTANCE == null) {
            if (ServerLifecycleHooks.getCurrentServer() == null)
                return null;

            DimensionDataStorage dimData = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
            DUNGEON_SD_INSTANCE = dimData.computeIfAbsent(new Factory<>(DungeonSavedData::new, DungeonSavedData::load), DungeonSavedData.ID);
        }
        return DUNGEON_SD_INSTANCE;
    }

    public static SoulNetwork getSoulNetwork(UUID uuid) {
        BMSavedData savedData = getSavedData();
        if (savedData == null)
            return null;

        return savedData.getNetwork(uuid);
    }

    public static SoulNetwork getSoulNetwork(Binding binding) {
        return getSoulNetwork(binding.uuid());
    }

    public static SoulNetwork getSoulNetwork(Player player) {
        return getSoulNetwork(player.getUUID());
    }

    public static SoulNetwork getSoulNetwork(String uuid) {
        return getSoulNetwork(UUID.fromString(uuid));
    }

    // ==================== Dungeon Helper Methods ====================

    /**
     * Gets the spawn position for the next dungeon instance.
     * Uses a spiral grid pattern to ensure dungeons don't overlap.
     * @return The BlockPos for spawning the next dungeon, or null if server not available
     */
    @Nullable
    public static BlockPos getSpawnPositionOfDungeon() {
        DungeonSavedData savedData = getDungeonSavedData();
        if (savedData == null)
            return null;

        return savedData.getNextDungeonSpawnPosition();
    }

    /**
     * Increments the dungeon counter after spawning a dungeon.
     * Call this after successfully creating a dungeon.
     */
    public static void incrementDungeonCounter() {
        DungeonSavedData savedData = getDungeonSavedData();
        if (savedData != null) {
            savedData.incrementDungeonCounter();
        }
    }

    /**
     * Gets the current number of dungeons that have been spawned.
     * @return The dungeon count, or 0 if server not available
     */
    public static int getNumberOfDungeons() {
        DungeonSavedData savedData = getDungeonSavedData();
        if (savedData == null)
            return 0;

        return savedData.getNumberOfDungeons();
    }
}
