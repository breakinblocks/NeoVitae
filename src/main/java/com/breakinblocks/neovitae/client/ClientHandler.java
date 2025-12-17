package com.breakinblocks.neovitae.client;

import net.minecraft.core.Direction;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.ritual.Ritual;

/**
 * Client-side handler for ritual hologram display and other client-only functionality.
 */
public class ClientHandler {

    private static MasterRitualStoneTile mrsHoloTile;
    private static Ritual mrsHoloRitual;
    private static Direction mrsHoloDirection;
    private static boolean mrsHoloDisplay;

    private static MasterRitualStoneTile mrsRangeTile;
    private static boolean mrsRangeDisplay;

    /**
     * Sets up ritual hologram display for the given master ritual stone.
     */
    public static void setRitualHolo(MasterRitualStoneTile masterRitualStone, Ritual ritual, Direction direction, boolean displayed) {
        mrsHoloDisplay = displayed;
        mrsHoloTile = masterRitualStone;
        mrsHoloRitual = ritual;
        mrsHoloDirection = direction;
    }

    /**
     * Clears the ritual hologram display.
     */
    public static void setRitualHoloToNull() {
        mrsHoloDisplay = false;
        mrsHoloTile = null;
        mrsHoloRitual = null;
        mrsHoloDirection = Direction.NORTH;
    }

    /**
     * Sets up ritual range hologram display for the given master ritual stone.
     */
    public static void setRitualRangeHolo(MasterRitualStoneTile masterRitualStone, boolean displayed) {
        mrsRangeDisplay = displayed;
        mrsRangeTile = masterRitualStone;
    }

    /**
     * Clears the ritual range hologram display.
     */
    public static void setRitualRangeHoloToNull() {
        mrsRangeDisplay = false;
        mrsRangeTile = null;
    }

    // Getters for the renderer
    public static MasterRitualStoneTile getHoloTile() {
        return mrsHoloTile;
    }

    public static Ritual getHoloRitual() {
        return mrsHoloRitual;
    }

    public static Direction getHoloDirection() {
        return mrsHoloDirection;
    }

    public static boolean isHoloDisplayed() {
        return mrsHoloDisplay;
    }

    public static MasterRitualStoneTile getRangeTile() {
        return mrsRangeTile;
    }

    public static boolean isRangeDisplayed() {
        return mrsRangeDisplay;
    }
}
