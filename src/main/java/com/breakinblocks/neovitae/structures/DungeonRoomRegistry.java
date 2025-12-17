package com.breakinblocks.neovitae.structures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for dungeon rooms and room pools.
 * Handles weighted random selection of rooms from pools.
 * Thread-safe implementation using concurrent collections.
 */
public final class DungeonRoomRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonRoomRegistry.class);

    private DungeonRoomRegistry() {}

    // Legacy weighted room map (for direct room registration) - thread-safe
    private static final Map<DungeonRoom, Integer> dungeonWeightMap = new ConcurrentHashMap<>();
    private static final Map<String, List<DungeonRoom>> dungeonStartingRoomMap = new ConcurrentHashMap<>();
    private static volatile int totalWeight = 0;

    // Modern room pool system - thread-safe
    private static final Map<ResourceLocation, DungeonRoom> dungeonRoomMap = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> roomPoolTable = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Integer> totalWeightMap = new ConcurrentHashMap<>();

    // Unloaded resources (to be loaded from JSON) - thread-safe
    private static final List<ResourceLocation> unloadedDungeonRooms = new CopyOnWriteArrayList<>();
    private static final List<ResourceLocation> unloadedDungeonRoomPools = new CopyOnWriteArrayList<>();

    /**
     * Gets the legacy dungeon weight map (read-only view).
     */
    public static Map<DungeonRoom, Integer> getDungeonWeightMap() {
        return Collections.unmodifiableMap(dungeonWeightMap);
    }

    /**
     * Gets the dungeon room map (read-only view).
     */
    public static Map<ResourceLocation, DungeonRoom> getDungeonRoomMap() {
        return Collections.unmodifiableMap(dungeonRoomMap);
    }

    /**
     * Gets the room pool table (read-only view).
     */
    public static Map<ResourceLocation, List<Pair<ResourceLocation, Integer>>> getRoomPoolTable() {
        return Collections.unmodifiableMap(roomPoolTable);
    }

    /**
     * Gets the list of unloaded dungeon rooms.
     */
    public static List<ResourceLocation> getUnloadedDungeonRooms() {
        return unloadedDungeonRooms;
    }

    /**
     * Gets the list of unloaded room pools.
     */
    public static List<ResourceLocation> getUnloadedDungeonRoomPools() {
        return unloadedDungeonRoomPools;
    }

    /**
     * Registers a dungeon room with a specific weight.
     */
    public static synchronized void registerDungeonRoom(ResourceLocation res, DungeonRoom room, int weight) {
        room.setKey(res);
        dungeonWeightMap.put(room, weight);
        totalWeight += weight;
        dungeonRoomMap.put(res, room);
        LOGGER.debug("Registered dungeon room: {} with weight {}", res, weight);
    }

    /**
     * Registers an unloaded dungeon room to be loaded from JSON.
     */
    public static void registerUnloadedDungeonRoom(ResourceLocation res) {
        unloadedDungeonRooms.add(res);
    }

    /**
     * Registers an unloaded room pool to be loaded from JSON.
     */
    public static void registerUnloadedDungeonRoomPool(ResourceLocation res) {
        unloadedDungeonRoomPools.add(res);
    }

    /**
     * Registers a room pool with weighted room entries.
     */
    public static void registerDungeonRoomPool(ResourceLocation poolRes, List<Pair<ResourceLocation, Integer>> pool) {
        roomPoolTable.put(poolRes, new CopyOnWriteArrayList<>(pool));
        int totalWeightOfPool = pool.stream().mapToInt(Pair::getValue).sum();
        totalWeightMap.put(poolRes, totalWeightOfPool);
        LOGGER.debug("Registered dungeon pool: {} with {} entries and total weight {}",
                poolRes, pool.size(), totalWeightOfPool);
    }

    /**
     * Gets a random dungeon room from a room pool.
     *
     * @param roomPoolName The resource location of the room pool
     * @param rand         Random source
     * @return A random DungeonRoom, or null if the pool doesn't exist or is empty
     */
    public static DungeonRoom getRandomDungeonRoom(ResourceLocation roomPoolName, RandomSource rand) {
        Integer maxWeight = totalWeightMap.get(roomPoolName);
        if (maxWeight == null || maxWeight <= 0) {
            LOGGER.warn("No weight found for room pool: {}", roomPoolName);
            return null;
        }

        int wantedWeight = rand.nextInt(maxWeight);
        List<Pair<ResourceLocation, Integer>> roomPool = roomPoolTable.get(roomPoolName);

        if (roomPool == null || roomPool.isEmpty()) {
            LOGGER.warn("Empty or missing room pool: {}", roomPoolName);
            return null;
        }

        for (Pair<ResourceLocation, Integer> entry : roomPool) {
            wantedWeight -= entry.getValue();
            if (wantedWeight < 0) {
                ResourceLocation dungeonName = entry.getKey();
                DungeonRoom room = dungeonRoomMap.get(dungeonName);
                if (room == null) {
                    LOGGER.warn("Room not found in registry: {}", dungeonName);
                }
                return room;
            }
        }

        return null;
    }

    /**
     * Gets a specific dungeon room by resource location.
     */
    public static DungeonRoom getDungeonRoom(ResourceLocation dungeonName) {
        return dungeonRoomMap.get(dungeonName);
    }

    /**
     * Registers a room as a starter room for a specific dungeon type.
     */
    public static void registerStarterDungeonRoom(DungeonRoom room, String key) {
        dungeonStartingRoomMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(room);
    }

    /**
     * Gets a random room from the global weighted pool (legacy method).
     */
    public static DungeonRoom getRandomDungeonRoom(RandomSource rand) {
        if (totalWeight <= 0 || dungeonWeightMap.isEmpty()) {
            return null;
        }

        int wantedWeight = rand.nextInt(totalWeight);
        for (Map.Entry<DungeonRoom, Integer> entry : dungeonWeightMap.entrySet()) {
            wantedWeight -= entry.getValue();
            if (wantedWeight < 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Gets a random starter room for a specific dungeon type.
     */
    public static DungeonRoom getRandomStarterDungeonRoom(Random rand, String key) {
        List<DungeonRoom> roomList = dungeonStartingRoomMap.get(key);
        if (roomList == null || roomList.isEmpty()) {
            return null;
        }
        return roomList.get(rand.nextInt(roomList.size()));
    }

    /**
     * Clears all registered rooms and pools.
     * Useful for reloading data.
     */
    public static synchronized void clear() {
        dungeonWeightMap.clear();
        dungeonStartingRoomMap.clear();
        dungeonRoomMap.clear();
        roomPoolTable.clear();
        totalWeightMap.clear();
        unloadedDungeonRooms.clear();
        unloadedDungeonRoomPools.clear();
        totalWeight = 0;
    }
}
