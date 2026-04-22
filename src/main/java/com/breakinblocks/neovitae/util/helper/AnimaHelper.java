package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.world.NVSavedData;
import com.breakinblocks.neovitae.common.world.DungeonSavedData;

import javax.annotation.Nullable;
import java.util.UUID;


@EventBusSubscriber
public class AnimaHelper {
    @Nullable
    private static NVSavedData SD_INSTANCE;

    @Nullable
    private static DungeonSavedData DUNGEON_SD_INSTANCE;

    @SubscribeEvent
    public static void resetSavedDataInstance(ServerStoppedEvent event) {
        SD_INSTANCE = null;
        DUNGEON_SD_INSTANCE = null;
    }

    @Nullable
    private static NVSavedData getSavedData() {
        if (SD_INSTANCE == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null)
                return null;
            SavedDataStorage dimData = server.overworld().getDataStorage();
            SD_INSTANCE = dimData.computeIfAbsent(NVSavedData.TYPE);
        }
        return SD_INSTANCE;
    }

    @Nullable
    private static DungeonSavedData getDungeonSavedData() {
        if (DUNGEON_SD_INSTANCE == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null)
                return null;
            SavedDataStorage dimData = server.overworld().getDataStorage();
            DUNGEON_SD_INSTANCE = dimData.computeIfAbsent(DungeonSavedData.TYPE);
        }
        return DUNGEON_SD_INSTANCE;
    }

    public static Anima getAnima(UUID uuid) {
        NVSavedData savedData = getSavedData();
        if (savedData == null)
            return null;
        return savedData.getNetwork(uuid);
    }

    public static Anima getAnima(Binding binding) {
        return getAnima(binding.uuid());
    }

    public static Anima getAnima(Player player) {
        return getAnima(player.getUUID());
    }

    public static Anima getAnima(String uuid) {
        return getAnima(UUID.fromString(uuid));
    }

    @Nullable
    public static BlockPos getSpawnPositionOfDungeon() {
        DungeonSavedData savedData = getDungeonSavedData();
        if (savedData == null)
            return null;
        return savedData.getNextDungeonSpawnPosition();
    }

    public static void incrementDungeonCounter() {
        DungeonSavedData savedData = getDungeonSavedData();
        if (savedData != null) {
            savedData.incrementDungeonCounter();
        }
    }

    public static int getNumberOfDungeons() {
        DungeonSavedData savedData = getDungeonSavedData();
        if (savedData == null)
            return 0;
        return savedData.getNumberOfDungeons();
    }
}
