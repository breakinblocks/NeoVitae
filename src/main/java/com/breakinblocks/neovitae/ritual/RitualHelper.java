package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
