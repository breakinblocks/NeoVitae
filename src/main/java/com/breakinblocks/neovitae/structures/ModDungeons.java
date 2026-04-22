package com.breakinblocks.neovitae.structures;

import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registry of all dungeon room resource locations.
 * These are used by DungeonRoomProvider during datagen to generate JSON files,
 * and by DungeonRoomLoader at runtime to load rooms.
 */
public class ModDungeons {
    // Corridor rooms
    public static final Identifier T_CORRIDOR = NeoVitae.rl("t_corridor");
    public static final Identifier FOUR_WAY_CORRIDOR_LOOT = NeoVitae.rl("four_way_corridor_loot");
    public static final Identifier FOUR_WAY_CORRIDOR = NeoVitae.rl("four_way_corridor");
    public static final Identifier OVERLAPPED_CORRIDOR = NeoVitae.rl("overlapped_corridor");
    public static final Identifier STRAIGHT_CORRIDOR = NeoVitae.rl("straight_corridor");
    public static final Identifier SPIRAL_STAIRCASE = NeoVitae.rl("spiral_staircase");

    // Mini dungeon rooms
    public static final Identifier MINI_LIBRARY = NeoVitae.rl("mini_dungeon/library");
    public static final Identifier MINI_ARMOURY = NeoVitae.rl("mini_dungeon/armoury");
    public static final Identifier MINI_FARM = NeoVitae.rl("mini_dungeon/farm");
    public static final Identifier MINI_PORTAL = NeoVitae.rl("mini_dungeon/portal_nether");
    public static final Identifier MINI_CRYPT = NeoVitae.rl("mini_dungeon/crypt");
    public static final Identifier MINI_ENTRANCE = NeoVitae.rl("t3_entrance");

    // Standard dungeon rooms
    public static final Identifier ORE_HOLD_1 = NeoVitae.rl("standard/ore_hold_1");
    public static final Identifier CHALLENGE_TOWER = NeoVitae.rl("standard/challenge_tower");
    public static final Identifier BIG_LIBRARY = NeoVitae.rl("standard/big_library");
    public static final Identifier SMALL_CRANE = NeoVitae.rl("standard/small_crane");
    public static final Identifier SMALL_LIBRARY = NeoVitae.rl("standard/small_library");
    public static final Identifier SMALL_SMITHY = NeoVitae.rl("standard/small_smithy");
    public static final Identifier TALL_SPIRAL = NeoVitae.rl("standard/tall_spiral");
    public static final Identifier SMALL_ARENA = NeoVitae.rl("standard/small_arena");
    public static final Identifier ANTECHAMBER = NeoVitae.rl("standard/antechamber");
    public static final Identifier DESTROYED_END_PORTAL = NeoVitae.rl("standard/destroyed_end_portal");
    public static final Identifier AUG_CORRIDOR_LOOT = NeoVitae.rl("standard/four_way_corridor_loot");
    public static final Identifier WATER_WAY = NeoVitae.rl("standard/water_way");
    public static final Identifier STANDARD_ENTRANCE = NeoVitae.rl("standard_entrance");

    // Mine rooms
    public static final Identifier MINE_KEY = NeoVitae.rl("mines/mine_key");
    public static final Identifier MINE_ENTRANCE = NeoVitae.rl("standard/mine_entrance");
    public static final Identifier MINE_PIT = NeoVitae.rl("mines/pit");
    public static final Identifier MINE_CORNER_ZOMBIE_TRAP = NeoVitae.rl("mines/corner_zombie_trap");
    public static final Identifier MINE_SPLIT_ROAD = NeoVitae.rl("mines/split_road");
    public static final Identifier MINE_STATION = NeoVitae.rl("mines/station");
    public static final Identifier MINE_DOWNWARD_TUNNEL = NeoVitae.rl("mines/downward_tunnel");
    public static final Identifier MINE_JUNCTION_STATION = NeoVitae.rl("mines/junction_station");
    public static final Identifier MINE_BUILT_SHAFT = NeoVitae.rl("mines/downward_shaft");
    public static final Identifier MINE_NATURE_CROSSROAD = NeoVitae.rl("mines/nature_crossroad");
    public static final Identifier MINE_WOLF_DEN = NeoVitae.rl("mines/wolf_den");
    public static final Identifier MINE_ORE_CAVERN = NeoVitae.rl("mines/ore_cavern");

    // Mine corridors
    public static final Identifier MINE_STRAIGHT_CORRIDOR = NeoVitae.rl("mines/straight_corridor");
    public static final Identifier MINE_BENT_CORRIDOR = NeoVitae.rl("mines/bent_corridor");
    public static final Identifier MINE_FOURWAY_CORRIDOR = NeoVitae.rl("mines/fourway_corridor");

    // Deadend rooms
    public static final Identifier DEFAULT_DEADEND = NeoVitae.rl("default_deadend");
    public static final Identifier MINES_DEADEND = NeoVitae.rl("mines/deadend");

    /**
     * Initialize dungeon rooms - registers unloaded rooms and triggers loading.
     */
    public static void init() {
        registerDungeonRooms();
        DungeonRoomLoader.loadDungeons();
    }

    /**
     * Register all dungeon rooms with the DungeonRoomRegistry.
     */
    public static void registerDungeonRooms() {
        // Corridor rooms
        DungeonRoomRegistry.registerUnloadedDungeonRoom(T_CORRIDOR);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(FOUR_WAY_CORRIDOR_LOOT);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(FOUR_WAY_CORRIDOR);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(ORE_HOLD_1);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(STRAIGHT_CORRIDOR);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(OVERLAPPED_CORRIDOR);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(SPIRAL_STAIRCASE);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(WATER_WAY);

        // Mine entrance and key
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_ENTRANCE);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_KEY);

        // Mine rooms
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_PIT);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_CORNER_ZOMBIE_TRAP);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_SPLIT_ROAD);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_STATION);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_DOWNWARD_TUNNEL);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_JUNCTION_STATION);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_BUILT_SHAFT);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_NATURE_CROSSROAD);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_WOLF_DEN);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_ORE_CAVERN);

        // Mine corridors
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_STRAIGHT_CORRIDOR);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_BENT_CORRIDOR);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINE_FOURWAY_CORRIDOR);

        // Mini dungeon rooms
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINI_LIBRARY);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINI_ARMOURY);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINI_FARM);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINI_PORTAL);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINI_CRYPT);

        // Standard rooms
        DungeonRoomRegistry.registerUnloadedDungeonRoom(CHALLENGE_TOWER);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(BIG_LIBRARY);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(SMALL_CRANE);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(SMALL_LIBRARY);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(SMALL_SMITHY);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(TALL_SPIRAL);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(SMALL_ARENA);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(ANTECHAMBER);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(DESTROYED_END_PORTAL);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(AUG_CORRIDOR_LOOT);

        // Entrance rooms
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINI_ENTRANCE);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(STANDARD_ENTRANCE);

        // Deadend rooms
        DungeonRoomRegistry.registerUnloadedDungeonRoom(DEFAULT_DEADEND);
        DungeonRoomRegistry.registerUnloadedDungeonRoom(MINES_DEADEND);
    }
}
