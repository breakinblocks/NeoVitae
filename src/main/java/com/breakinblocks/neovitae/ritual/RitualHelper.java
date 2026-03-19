package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.BloodAltarTile;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class providing common operations used by rituals.
 * Eliminates code duplication across ritual implementations.
 *
 * <p>Common patterns extracted:
 * <ul>
 *   <li>Server-side validation</li>
 *   <li>Essence availability checking</li>
 *   <li>Range fallback logic</li>
 *   <li>Entity enumeration within ranges</li>
 *   <li>LP syphoning with cost capping</li>
 * </ul>
 */
public final class RitualHelper {

    private RitualHelper() {} // Utility class - no instantiation
    

    /**
     * Creates a ritual context containing all commonly-needed data for ritual execution.
     * Returns null if the ritual cannot execute (client-side, no network, etc.).
     *
     * @param masterRitualStone the master ritual stone
     * @param minEssence minimum essence required (usually getRefreshCost())
     * @return context if ritual can execute, null otherwise
     */
    @Nullable
    public static RitualContext createContext(IMasterRitualStone masterRitualStone, int minEssence) {
        Level level = masterRitualStone.getLevel();
        if (level == null || level.isClientSide()) {
            return null;
        }

        SoulNetwork network = masterRitualStone.getOwnerNetwork();
        if (network == null) {
            return null;
        }

        int currentEssence = network.getCurrentEssence();
        if (currentEssence < minEssence) {
            return null;
        }

        return new RitualContext(level, network, currentEssence, masterRitualStone.getBlockPos(), masterRitualStone);
    }

    /**
     * Creates a ritual context without checking minimum essence.
     * Useful for rituals that can partially execute with any essence.
     *
     * @param masterRitualStone the master ritual stone
     * @return context if ritual can execute, null otherwise
     */
    @Nullable
    public static RitualContext createContext(IMasterRitualStone masterRitualStone) {
        return createContext(masterRitualStone, 0);
    }

    // RANGE OPERATIONS 
    
    /**
     * Gets the effective range for a ritual, checking the master's customized range first
     * and falling back to the ritual's default if not set.
     *
     * @param masterRitualStone the master ritual stone (may have customized ranges)
     * @param ritual the ritual (has default ranges)
     * @param rangeKey the key identifying the range
     * @return the effective area descriptor, never null if key is valid
     */
    public static AreaDescriptor getEffectiveRange(IMasterRitualStone masterRitualStone, Ritual ritual, String rangeKey) {
        AreaDescriptor range = masterRitualStone.getBlockRange(rangeKey);
        if (range == null) {
            range = ritual.getBlockRange(rangeKey);
        }
        return range;
    }

    /**
     * Gets the AABB for a range, handling the fallback logic.
     *
     * @param masterRitualStone the master ritual stone
     * @param ritual the ritual
     * @param rangeKey the key identifying the range
     * @param masterPos the position of the master ritual stone
     * @return the AABB for the range, or null if range not found
     */
    @Nullable
    public static AABB getRangeAABB(IMasterRitualStone masterRitualStone, Ritual ritual, String rangeKey, BlockPos masterPos) {
        AreaDescriptor range = getEffectiveRange(masterRitualStone, ritual, rangeKey);
        return range != null ? range.getAABB(masterPos) : null;
    }

    /**
     * Gets contained positions for a range, handling the fallback logic.
     *
     * @param masterRitualStone the master ritual stone
     * @param ritual the ritual
     * @param rangeKey the key identifying the range
     * @param masterPos the position of the master ritual stone
     * @return list of contained positions, or empty list if range not found
     */
    public static List<BlockPos> getRangePositions(IMasterRitualStone masterRitualStone, Ritual ritual,
            String rangeKey, BlockPos masterPos) {
        AreaDescriptor range = getEffectiveRange(masterRitualStone, ritual, rangeKey);
        return range != null ? range.getContainedPositions(masterPos) : Collections.emptyList();
    }

    // ENTITY OPERATIONS 
    

    /**
     * Gets all entities of a specific type within a ritual's range.
     *
     * @param context the ritual context
     * @param ritual the ritual
     * @param rangeKey the key identifying the range
     * @param entityClass the class of entities to find
     * @return list of entities in range, or empty list if range not found
     */
    public static <T extends Entity> List<T> getEntitiesInRange(RitualContext context, Ritual ritual,
            String rangeKey, Class<T> entityClass) {
        AABB aabb = getRangeAABB(context.master(), ritual, rangeKey, context.masterPos());
        if (aabb == null) {
            return Collections.emptyList();
        }
        return context.level().getEntitiesOfClass(entityClass, aabb);
    }

    /**
     * Gets all entities of a specific type within a ritual's range with a filter.
     *
     * @param context the ritual context
     * @param ritual the ritual
     * @param rangeKey the key identifying the range
     * @param entityClass the class of entities to find
     * @param filter predicate to filter entities
     * @return list of filtered entities in range
     */
    public static <T extends Entity> List<T> getEntitiesInRange(RitualContext context, Ritual ritual,
            String rangeKey, Class<T> entityClass, java.util.function.Predicate<T> filter) {
        AABB aabb = getRangeAABB(context.master(), ritual, rangeKey, context.masterPos());
        if (aabb == null) {
            return Collections.emptyList();
        }
        return context.level().getEntitiesOfClass(entityClass, aabb, filter);
    }

    // ALTAR OPERATIONS

    /**
     * Finds a BloodAltarTile within a ritual's range, using a cached offset if available.
     * Used by rituals that need to feed LP directly to an altar (FeatheredKnife, WellOfSuffering).
     *
     * @param context the ritual context
     * @param ritual the ritual
     * @param rangeKey the key identifying the altar search range
     * @param cachedOffset cached offset from a previous search, or null
     * @return a result containing the altar (if found) and the updated offset
     */
    public static AltarSearchResult findAltar(RitualContext context, Ritual ritual,
            String rangeKey, @Nullable BlockPos cachedOffset) {
        BlockPos masterPos = context.masterPos();

        // Try cached position first
        if (cachedOffset != null) {
            BlockPos altarPos = masterPos.offset(cachedOffset);
            BlockEntity be = context.level().getBlockEntity(altarPos);
            if (be instanceof BloodAltarTile altarTile) {
                return new AltarSearchResult(altarTile, cachedOffset);
            }
        }

        // Search for altar in range
        List<BlockPos> positions = getRangePositions(context.master(), ritual, rangeKey, masterPos);
        for (BlockPos pos : positions) {
            BlockEntity be = context.level().getBlockEntity(pos);
            if (be instanceof BloodAltarTile altarTile) {
                return new AltarSearchResult(altarTile, pos.subtract(masterPos));
            }
        }

        return new AltarSearchResult(null, null);
    }

    /**
     * Result of an altar search, containing the found altar and its offset.
     */
    public record AltarSearchResult(@Nullable BloodAltarTile altar, @Nullable BlockPos offset) {}

    /**
     * Reads a cached altar offset from NBT.
     */
    @Nullable
    public static BlockPos readAltarOffset(CompoundTag tag) {
        if (tag.contains("altarOffsetX")) {
            return new BlockPos(
                    tag.getInt("altarOffsetX"),
                    tag.getInt("altarOffsetY"),
                    tag.getInt("altarOffsetZ")
            );
        }
        return null;
    }

    /**
     * Writes a cached altar offset to NBT.
     */
    public static void writeAltarOffset(CompoundTag tag, @Nullable BlockPos offset) {
        if (offset != null) {
            tag.putInt("altarOffsetX", offset.getX());
            tag.putInt("altarOffsetY", offset.getY());
            tag.putInt("altarOffsetZ", offset.getZ());
        }
    }

    // DEMON WILL OPERATIONS

    /**
     * Queries all demon will types at once for a ritual position.
     * Eliminates repeated getCurrentWill() calls across rituals.
     *
     * @param level the level
     * @param pos the ritual master position
     * @return map of will types to their current values
     */
    public static Map<EnumWillType, Double> queryAllWill(Level level, BlockPos pos) {
        Map<EnumWillType, Double> will = new EnumMap<>(EnumWillType.class);
        for (EnumWillType type : EnumWillType.values()) {
            will.put(type, WorldDemonWillHandler.getCurrentWill(level, pos, type));
        }
        return will;
    }

    /**
     * Drains accumulated will consumption from a ritual.
     * Convenience method to avoid repeating the drain-if-positive pattern.
     *
     * @param level the level
     * @param pos the ritual master position
     * @param willUsed map of will types to amounts consumed
     */
    public static void drainAllWill(Level level, BlockPos pos, Map<EnumWillType, Double> willUsed) {
        willUsed.forEach((type, amount) -> {
            if (amount > 0) {
                WorldDemonWillHandler.drainWillFromChunk(level, pos, type, amount);
            }
        });
    }

    // LP OPERATIONS
    
    /**
     * Syphons LP from the network, capping at the available essence.
     *
     * @param context the ritual context
     * @param cost the desired LP cost
     */
    public static void syphonLP(RitualContext context, int cost) {
        if (cost > 0) {
            int actualCost = Math.min(cost, context.currentEssence());
            context.network().syphon(context.master().ticket(actualCost));
        }
    }

    /**
     * Calculates the maximum number of operations possible with available essence.
     *
     * @param context the ritual context
     * @param costPerOperation LP cost for each operation
     * @return maximum operations possible
     */
    public static int getMaxOperations(RitualContext context, int costPerOperation) {
        if (costPerOperation <= 0) return Integer.MAX_VALUE;
        return context.currentEssence() / costPerOperation;
    }

    // CONTEXT RECORD 
    
    /**
     * Encapsulates common data needed for ritual execution.
     * Created once at the start of performRitual() and passed to helper methods.
     *
     * @param level the server level
     * @param network the owner's soul network
     * @param currentEssence current LP available
     * @param masterPos position of the master ritual stone
     * @param master the master ritual stone interface
     */
    public record RitualContext(
            Level level,
            SoulNetwork network,
            int currentEssence,
            BlockPos masterPos,
            IMasterRitualStone master
    ) {
        /**
         * Convenience method to syphon LP.
         */
        public void syphon(int cost) {
            RitualHelper.syphonLP(this, cost);
        }

        /**
         * Convenience method to get max operations.
         */
        public int maxOperations(int costPerOperation) {
            return RitualHelper.getMaxOperations(this, costPerOperation);
        }
    }
}
