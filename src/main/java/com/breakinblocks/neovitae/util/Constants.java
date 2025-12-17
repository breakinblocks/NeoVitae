package com.breakinblocks.neovitae.util;

/**
 * Constants used throughout Blood Magic.
 */
public class Constants {

    public static class NBT {
        // Coordinate keys
        public static final String X_COORD = "xCoord";
        public static final String Y_COORD = "yCoord";
        public static final String Z_COORD = "zCoord";

        // Owner keys
        public static final String OWNER_UUID = "ownerUUID";
        public static final String OWNER_NAME = "ownerNAME";

        // Binding
        public static final String ACTIVATED = "activated";

        // Altar
        public static final String CURRENT_ESSENCE = "currentEssence";

        // Ritual
        public static final String CURRENT_RITUAL = "currentRitual";
        public static final String CURRENT_RITUAL_TAG = "currentRitualTag";
        public static final String IS_RUNNING = "isRunning";
        public static final String DIRECTION = "direction";
        public static final String RUNTIME = "runtime";

        // Teleposition
        public static final String DIMENSION_ID = "dimensionId";
        public static final String PORTAL_LOCATION = "portalLocation";

        // Routing
        public static final String ROUTING_MASTER = "master";
        public static final String ROUTING_CONNECTION = "connections";
        public static final String ROUTING_PRIORITY = "prioritiesPeople";
        public static final String ROUTING_MASTER_GENERAL = "generalList";
        public static final String ROUTING_MASTER_INPUT = "inputList";
        public static final String ROUTING_MASTER_OUTPUT = "outputList";
        public static final String ROUTING_MASTER_FLUID_INPUT = "fluidInputList";
        public static final String ROUTING_MASTER_FLUID_OUTPUT = "fluidOutputList";

        // Filter
        public static final String FILTER_INV = "filterInventory";
        public static final String FILTER_SLOT = "filterSlot";

        // Block entity
        public static final String ITEMS = "items";
        public static final String SLOT = "slot";

        // Dungeon System
        public static final String DUNGEON_EXIT = "neovitae:dungeon_exit";
        public static final String DUNGEON_TELEPORT_POS = "dungeonTeleportPos";
        public static final String DUNGEON_TELEPORT_KEY = "dungeonTeleportKey";
        public static final String DOOR_MAP = "doorMap";
        public static final String AREA_DESCRIPTORS = "areaDescriptors";
        public static final String ROOM_POOL = "roomPool";
        public static final String ROOM_POOL_BUFFER = "roomPoolBuffer";
        public static final String ROOM_POOL_TRACKER = "roomPoolTracker";
        public static final String VALUE = "value";

        // Dungeon Controller
        public static final String CONTROLLER_POS = "controllerPos";
        public static final String DOOR_POS = "doorPos";
        public static final String DOOR_DIRECTION = "doorDirection";
        public static final String DOOR_TYPE = "doorType";
        public static final String POTENTIAL_ROOM_TYPES = "potentialRoomTypes";
        public static final String SYNTHESIZER = "synthesizer";
    }
}
