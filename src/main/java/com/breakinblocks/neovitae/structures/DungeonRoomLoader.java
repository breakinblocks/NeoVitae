package com.breakinblocks.neovitae.structures;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.gson.Serializers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Loads dungeon rooms and room pools from JSON resource files.
 */
public final class DungeonRoomLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonRoomLoader.class);

    private DungeonRoomLoader() {}

    /**
     * Loads all registered room pools from JSON resources.
     */
    public static void loadRoomPools() {
        for (ResourceLocation schematic : DungeonRoomRegistry.getUnloadedDungeonRoomPools()) {
            try {
                URL roomPoolURL = DungeonRoomLoader.class.getResource(resLocToResourcePath(schematic));
                if (roomPoolURL == null) {
                    LOGGER.warn("Could not find room pool resource: {}", schematic);
                    continue;
                }

                List<String> roomPoolList = Serializers.GSON.fromJson(
                        Resources.toString(roomPoolURL, Charsets.UTF_8),
                        new TypeToken<List<String>>() {}.getType()
                );

                List<Pair<ResourceLocation, Integer>> roomPool = new ArrayList<>();
                for (String roomEntryString : roomPoolList) {
                    Pair<ResourceLocation, Integer> roomEntry = parseRoomEntryString(roomEntryString);
                    if (roomEntry != null) {
                        roomPool.add(roomEntry);
                    }
                }

                DungeonRoomRegistry.registerDungeonRoomPool(schematic, roomPool);
                LOGGER.debug("Loaded room pool: {} with {} entries", schematic, roomPool.size());

            } catch (Exception e) {
                LOGGER.error("Failed to load room pool: {}", schematic, e);
            }
        }
    }

    /**
     * Parses a room entry string in format "weight;resourcelocation".
     */
    public static Pair<ResourceLocation, Integer> parseRoomEntryString(String str) {
        String[] splitString = str.split(";");
        if (splitString.length == 2) {
            try {
                Integer weight = Integer.parseInt(splitString[0]);
                ResourceLocation resLoc = ResourceLocation.parse(splitString[1]);
                return Pair.of(resLoc, weight);
            } catch (NumberFormatException ex) {
                LOGGER.error("Invalid weight in room entry: {}", str, ex);
            }
        }
        return null;
    }

    /**
     * Loads all registered dungeon rooms from JSON resources.
     */
    public static void loadDungeons() {
        for (ResourceLocation schematic : DungeonRoomRegistry.getUnloadedDungeonRooms()) {
            try {
                URL dungeonURL = DungeonRoomLoader.class.getResource(resLocToResourcePath(schematic));
                if (dungeonURL == null) {
                    LOGGER.warn("Could not find dungeon resource: {}", schematic);
                    continue;
                }

                LOGGER.debug("Loading schematic: {}", schematic);
                DungeonRoom dungeonRoom = Serializers.GSON.fromJson(
                        Resources.toString(dungeonURL, Charsets.UTF_8),
                        DungeonRoom.class
                );
                DungeonRoomRegistry.registerDungeonRoom(schematic, dungeonRoom, Math.max(1, dungeonRoom.getDungeonWeight()));
                LOGGER.debug("Loaded dungeon room: {}", schematic);

            } catch (Exception e) {
                LOGGER.error("Failed to load dungeon: {}", schematic, e);
            }
        }

        LOGGER.info("Loaded {} dungeon schematics", DungeonRoomRegistry.getUnloadedDungeonRooms().size());
    }

    /**
     * Saves all registered dungeons to config files (debug utility).
     */
    public static void saveDungeons() {
        for (DungeonRoom room : DungeonRoomRegistry.getDungeonWeightMap().keySet()) {
            saveSingleDungeon(room);
        }
    }

    /**
     * Saves a single dungeon room to a config file (debug utility).
     */
    public static void saveSingleDungeon(DungeonRoom room) {
        String json = Serializers.GSON.toJson(room);

        try {
            File file = new File("config/NeoVitae/schematics");
            file.mkdirs();

            Writer writer = new FileWriter("config/NeoVitae/schematics/" + new Random().nextInt() + ".json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Failed to save dungeon schematic", e);
        }
    }

    /**
     * Saves a room pool to a config file (debug utility).
     */
    public static void writeTestPool(List<Pair<String, Integer>> roomPool) {
        List<String> reducedRoomPool = new ArrayList<>();
        for (Pair<String, Integer> entry : roomPool) {
            reducedRoomPool.add(entry.getRight() + ";" + entry.getLeft());
        }

        String json = Serializers.GSON.toJson(reducedRoomPool);

        try {
            File file = new File("config/NeoVitae/schematics");
            file.mkdirs();

            Writer writer = new FileWriter("config/NeoVitae/schematics/" + Math.abs(new Random().nextInt()) + ".json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Failed to save room pool", e);
        }
    }

    /**
     * Converts a ResourceLocation to the resource path for JSON schematics.
     */
    public static String resLocToResourcePath(ResourceLocation resourceLocation) {
        return "/assets/" + resourceLocation.getNamespace() + "/schematics/" + resourceLocation.getPath() + ".json";
    }
}
