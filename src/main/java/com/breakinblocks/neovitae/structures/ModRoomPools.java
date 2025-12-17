package com.breakinblocks.neovitae.structures;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Defines the resource locations for all dungeon room pools and rooms.
 * Room pools are JSON files that define weighted lists of rooms.
 * Individual rooms are JSON files defining room structures and door positions.
 */
public final class ModRoomPools {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModRoomPools.class);

    private ModRoomPools() {}

    // ==================== Room Pools ====================

    // Connector corridors between rooms
    public static final ResourceLocation CONNECTIVE_CORRIDORS =
            NeoVitae.rl("room_pools/connective_corridors");

    // Entrance rooms for different dungeon types
    public static final ResourceLocation MINI_DUNGEON_ENTRANCES =
            NeoVitae.rl("room_pools/entrances/mini_dungeon_entrances");
    public static final ResourceLocation STANDARD_DUNGEON_ENTRANCES =
            NeoVitae.rl("room_pools/entrances/standard_dungeon_entrances");

    // Mini dungeon tier 1 rooms
    public static final ResourceLocation MINI_DUNGEON =
            NeoVitae.rl("room_pools/tier1/mini_dungeon");

    // Standard dungeon rooms
    public static final ResourceLocation STANDARD_ROOMS =
            NeoVitae.rl("room_pools/standard/standard_rooms");
    public static final ResourceLocation STANDARD_DEADEND =
            NeoVitae.rl("room_pools/standard/standard_deadend");

    // Mine special area rooms
    public static final ResourceLocation MINE_ENTRANCES =
            NeoVitae.rl("room_pools/special/mine_entrances");
    public static final ResourceLocation MINE_KEY =
            NeoVitae.rl("room_pools/standard/mine_key");
    public static final ResourceLocation MINE_ROOMS =
            NeoVitae.rl("room_pools/mines/mine_rooms");
    public static final ResourceLocation MINE_CORRIDORS =
            NeoVitae.rl("room_pools/mines/mine_corridors");
    public static final ResourceLocation MINE_DEADEND =
            NeoVitae.rl("room_pools/mines/mine_deadend");

    // Default dead-end pool
    public static final ResourceLocation DEFAULT_DEADEND = STANDARD_DEADEND;

    /**
     * Initializes all room pools and rooms by registering them with the DungeonRoomRegistry.
     * Called during mod initialization (FMLCommonSetupEvent).
     */
    public static void init() {
        LOGGER.info("Initializing dungeon room pools and rooms...");
        registerRoomPools();
        ModDungeons.registerDungeonRooms();
        DungeonRoomLoader.loadRoomPools();
        DungeonRoomLoader.loadDungeons();
        LOGGER.info("Dungeon initialization complete. Loaded {} rooms and {} pools.",
                DungeonRoomRegistry.getDungeonRoomMap().size(),
                DungeonRoomRegistry.getRoomPoolTable().size());
    }

    /**
     * Registers all room pool resource locations (to be loaded from JSON).
     */
    private static void registerRoomPools() {
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(CONNECTIVE_CORRIDORS);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINI_DUNGEON_ENTRANCES);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(STANDARD_DUNGEON_ENTRANCES);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINI_DUNGEON);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(STANDARD_ROOMS);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINE_ENTRANCES);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINE_ROOMS);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINE_CORRIDORS);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINE_KEY);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(DEFAULT_DEADEND);
        DungeonRoomRegistry.registerUnloadedDungeonRoomPool(MINE_DEADEND);
    }


    /**
     * Registers special room pool configurations.
     * Called after blocks are registered.
     */
    public static void registerSpecialRooms() {
        // Register special room pools with minimum room/depth requirements
        // These will use the default DUNGEON_SEAL block state
        SpecialDungeonRoomPoolRegistry.registerUniqueRoomPool(MINE_ENTRANCES, 5, 2);
        SpecialDungeonRoomPoolRegistry.registerUniqueRoomPool(MINE_KEY, 10, 3);
    }
}
