package com.breakinblocks.neovitae.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.breakinblocks.neovitae.NeoVitae;

public class DungeonSavedData extends SavedData {
    public static final String ID = "dungeons";
    public static final int DUNGEON_DISPLACEMENT = 1000;

    public static final Codec<DungeonSavedData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.optionalFieldOf("numberOfDungeons", 0).forGetter(DungeonSavedData::getNumberOfDungeons)
    ).apply(builder, DungeonSavedData::new));

    public static final SavedDataType<DungeonSavedData> TYPE = new SavedDataType<>(NeoVitae.rl(ID), DungeonSavedData::new, CODEC, DataFixTypes.LEVEL);

    private int numberOfDungeons;

    public DungeonSavedData() {
        this(0);
    }

    public DungeonSavedData(int numberOfDungeons) {
        this.numberOfDungeons = numberOfDungeons;
    }

    public int getNumberOfDungeons() {
        return numberOfDungeons;
    }

    public int incrementDungeonCounter() {
        numberOfDungeons++;
        setDirty();
        return numberOfDungeons;
    }

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
}
