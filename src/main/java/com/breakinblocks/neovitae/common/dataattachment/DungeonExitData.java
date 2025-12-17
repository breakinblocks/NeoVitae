package com.breakinblocks.neovitae.common.dataattachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Data attachment record storing a player's exit location from a dungeon.
 * Uses Codec-based serialization for modern NeoForge patterns.
 */
public record DungeonExitData(
        Optional<BlockPos> exitPos,
        Optional<ResourceKey<Level>> exitDimension
) {
    /**
     * Empty instance representing no stored exit data.
     */
    public static final DungeonExitData EMPTY = new DungeonExitData(Optional.empty(), Optional.empty());

    /**
     * Codec for serialization.
     */
    public static final Codec<DungeonExitData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.optionalFieldOf("exitPos").forGetter(DungeonExitData::exitPos),
                    ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("exitDimension").forGetter(DungeonExitData::exitDimension)
            ).apply(instance, DungeonExitData::new)
    );

    /**
     * Creates a new DungeonExitData with the given position and dimension.
     */
    public static DungeonExitData of(BlockPos pos, ResourceKey<Level> dimension) {
        return new DungeonExitData(Optional.of(pos), Optional.of(dimension));
    }

    /**
     * Creates a new DungeonExitData from a level and position.
     */
    public static DungeonExitData of(Level level, BlockPos pos) {
        return of(pos, level.dimension());
    }

    /**
     * Checks if this exit data is valid (has both position and dimension).
     */
    public boolean isValid() {
        return exitPos.isPresent() && exitDimension.isPresent();
    }

    /**
     * Gets the exit position, or null if not set.
     */
    public BlockPos getExitPosOrNull() {
        return exitPos.orElse(null);
    }

    /**
     * Gets the exit dimension, or null if not set.
     */
    public ResourceKey<Level> getExitDimensionOrNull() {
        return exitDimension.orElse(null);
    }

    /**
     * Clears this data, returning an empty instance.
     */
    public DungeonExitData clear() {
        return EMPTY;
    }
}
