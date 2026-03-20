package com.breakinblocks.neovitae.client;

import net.minecraft.core.Direction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.Ritual;

@OnlyIn(Dist.CLIENT)
public class ClientHandler {

    private static MasterRitualStoneBlockEntity mrsHoloTile;
    private static Ritual mrsHoloRitual;
    private static Direction mrsHoloDirection;
    private static boolean mrsHoloDisplay;

    private static MasterRitualStoneBlockEntity mrsRangeTile;
    private static boolean mrsRangeDisplay;

    public static void setRitualHolo(MasterRitualStoneBlockEntity masterRitualStone, Ritual ritual, Direction direction, boolean displayed) {
        mrsHoloDisplay = displayed;
        mrsHoloTile = masterRitualStone;
        mrsHoloRitual = ritual;
        mrsHoloDirection = direction;
    }

    public static void setRitualHoloToNull() {
        mrsHoloDisplay = false;
        mrsHoloTile = null;
        mrsHoloRitual = null;
        mrsHoloDirection = Direction.NORTH;
    }

    public static void setRitualRangeHolo(MasterRitualStoneBlockEntity masterRitualStone, boolean displayed) {
        mrsRangeDisplay = displayed;
        mrsRangeTile = masterRitualStone;
    }

    public static void setRitualRangeHoloToNull() {
        mrsRangeDisplay = false;
        mrsRangeTile = null;
    }

    public static MasterRitualStoneBlockEntity getHoloTile() {
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

    public static MasterRitualStoneBlockEntity getRangeTile() {
        return mrsRangeTile;
    }

    public static boolean isRangeDisplayed() {
        return mrsRangeDisplay;
    }
}
