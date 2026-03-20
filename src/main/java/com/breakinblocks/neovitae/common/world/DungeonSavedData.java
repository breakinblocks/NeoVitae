package com.breakinblocks.neovitae.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class DungeonSavedData extends SavedData {
    public static final String ID = "neovitae_dungeons";
    public static final int DUNGEON_DISPLACEMENT = 1000;

    private int numberOfDungeons = 0;

    public int getNumberOfDungeons() {
        return numberOfDungeons;
    }

    /**
     * Increments the dungeon counter and marks dirty.
     * @return The new dungeon index (to be used for positioning)
     */
    public int incrementDungeonCounter() {
        numberOfDungeons++;
        setDirty();
        return numberOfDungeons;
    }

    /**
     * Calculates the spawn position for the next dungeon based on a spiral grid pattern.
     * Each dungeon is placed DUNGEON_DISPLACEMENT blocks apart in a spiral.
     * @return The BlockPos for the dungeon controller in the dungeon dimension
     */
    public BlockPos getNextDungeonSpawnPosition() {
        int dungeonIndex = numberOfDungeons + 1;

        int gridIndexSize = (int) Math.ceil((Math.sqrt(dungeonIndex) - 1) / 2);

        int ringPlacementIndex = dungeonIndex;
        if (gridIndexSize > 0) {
            int innerSquare = (2 * (gridIndexSize - 1) + 1) * (2 * (gridIndexSize - 1) + 1);
            ringPlacementIndex = dungeonIndex - innerSquare;
        }

        for (int i = -gridIndexSize; i <= gridIndexSize; i++) {
            for (int j = -gridIndexSize; j <= gridIndexSize; j++) {
                if (Math.abs(i) != gridIndexSize && Math.abs(j) != gridIndexSize) {
                    continue;
                }

                ringPlacementIndex--;
                if (ringPlacementIndex == 0) {
                    return new BlockPos(i * DUNGEON_DISPLACEMENT, 64, j * DUNGEON_DISPLACEMENT);
                }
            }
        }

        return new BlockPos(0, 64, 0);
    }
    
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("numberOfDungeons", numberOfDungeons);
        return tag;
    }

    public static DungeonSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        DungeonSavedData savedData = new DungeonSavedData();
        savedData.numberOfDungeons = tag.getInt("numberOfDungeons");
        return savedData;
    }
}
