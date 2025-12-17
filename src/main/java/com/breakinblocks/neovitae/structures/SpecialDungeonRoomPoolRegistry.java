package com.breakinblocks.neovitae.structures;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.BMBlocks;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * Registry for special dungeon room pools that appear based on dungeon progression.
 */
public final class SpecialDungeonRoomPoolRegistry {

    private SpecialDungeonRoomPoolRegistry() {}

    private static final Map<ResourceLocation, BiPredicate<Integer, Integer>> predicateMap = new HashMap<>();
    private static final Map<ResourceLocation, BlockState> stateMap = new HashMap<>();

    /**
     * Gets a list of special rooms that should be added to the dungeon buffer
     * based on the current progression.
     *
     * @param minimumRooms        Number of rooms placed
     * @param minimumDepth        Current dungeon depth
     * @param timeSincePlacement  Map of room pool to rooms since last placement
     * @param bufferRoomPools     Current buffer of special room pools
     * @return List of new special room pools to add
     */
    public static List<ResourceLocation> getSpecialRooms(int minimumRooms, int minimumDepth,
                                                          Map<ResourceLocation, Integer> timeSincePlacement,
                                                          List<ResourceLocation> bufferRoomPools) {
        List<ResourceLocation> specialRoomPools = new ArrayList<>();

        for (Map.Entry<ResourceLocation, BiPredicate<Integer, Integer>> entry : predicateMap.entrySet()) {
            ResourceLocation roomPool = entry.getKey();

            // Skip if already in buffer or recently placed
            if (bufferRoomPools.contains(roomPool) || timeSincePlacement.containsKey(roomPool)) {
                continue;
            }

            if (entry.getValue().test(minimumRooms, minimumDepth)) {
                specialRoomPools.add(roomPool);
            }
        }

        return specialRoomPools;
    }

    /**
     * Registers a unique room pool with minimum room and depth requirements.
     */
    public static void registerUniqueRoomPool(ResourceLocation roomPool, int minRooms, int minDepth) {
        predicateMap.put(roomPool, (rooms, depth) -> rooms >= minRooms && depth >= minDepth);
    }

    /**
     * Registers a unique room pool with a custom seal block state.
     */
    public static void registerUniqueRoomPool(ResourceLocation roomPool, int minRooms, int minDepth, BlockState placementState) {
        registerUniqueRoomPool(roomPool, minRooms, minDepth);
        stateMap.put(roomPool, placementState);
    }

    /**
     * Gets the block state to use for the seal block for a special room pool.
     */
    public static BlockState getSealBlockState(ResourceLocation roomPool) {
        if (stateMap.containsKey(roomPool)) {
            return stateMap.get(roomPool);
        }
        // Use the dungeon seal block as default
        return BMBlocks.DUNGEON_SEAL.block().get().defaultBlockState();
    }

    /**
     * Clears all registered room pools.
     */
    public static void clear() {
        predicateMap.clear();
        stateMap.clear();
    }
}
