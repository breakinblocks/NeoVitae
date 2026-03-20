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
 */
public final class RitualHelper {

    private RitualHelper() {}

    /**
     * Creates a ritual context. Returns null if the ritual cannot execute
     * (client-side, no network, or insufficient essence).
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

    @Nullable
    public static RitualContext createContext(IMasterRitualStone masterRitualStone) {
        return createContext(masterRitualStone, 0);
    }

    /**
     * Gets the effective range, checking the master's customized range first
     * and falling back to the ritual's default if not set.
     */
    public static AreaDescriptor getEffectiveRange(IMasterRitualStone masterRitualStone, Ritual ritual, String rangeKey) {
        AreaDescriptor range = masterRitualStone.getBlockRange(rangeKey);
        if (range == null) {
            range = ritual.getBlockRange(rangeKey);
        }
        return range;
    }

    @Nullable
    public static AABB getRangeAABB(IMasterRitualStone masterRitualStone, Ritual ritual, String rangeKey, BlockPos masterPos) {
        AreaDescriptor range = getEffectiveRange(masterRitualStone, ritual, rangeKey);
        return range != null ? range.getAABB(masterPos) : null;
    }

    public static List<BlockPos> getRangePositions(IMasterRitualStone masterRitualStone, Ritual ritual,
            String rangeKey, BlockPos masterPos) {
        AreaDescriptor range = getEffectiveRange(masterRitualStone, ritual, rangeKey);
        return range != null ? range.getContainedPositions(masterPos) : Collections.emptyList();
    }

    public static <T extends Entity> List<T> getEntitiesInRange(RitualContext context, Ritual ritual,
            String rangeKey, Class<T> entityClass) {
        AABB aabb = getRangeAABB(context.master(), ritual, rangeKey, context.masterPos());
        if (aabb == null) {
            return Collections.emptyList();
        }
        return context.level().getEntitiesOfClass(entityClass, aabb);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(RitualContext context, Ritual ritual,
            String rangeKey, Class<T> entityClass, java.util.function.Predicate<T> filter) {
        AABB aabb = getRangeAABB(context.master(), ritual, rangeKey, context.masterPos());
        if (aabb == null) {
            return Collections.emptyList();
        }
        return context.level().getEntitiesOfClass(entityClass, aabb, filter);
    }

    /**
     * Finds a BloodAltarTile within a ritual's range, using a cached offset if available.
     */
    public static AltarSearchResult findAltar(RitualContext context, Ritual ritual,
            String rangeKey, @Nullable BlockPos cachedOffset) {
        BlockPos masterPos = context.masterPos();

        if (cachedOffset != null) {
            BlockPos altarPos = masterPos.offset(cachedOffset);
            BlockEntity be = context.level().getBlockEntity(altarPos);
            if (be instanceof BloodAltarTile altarTile) {
                return new AltarSearchResult(altarTile, cachedOffset);
            }
        }

        List<BlockPos> positions = getRangePositions(context.master(), ritual, rangeKey, masterPos);
        for (BlockPos pos : positions) {
            BlockEntity be = context.level().getBlockEntity(pos);
            if (be instanceof BloodAltarTile altarTile) {
                return new AltarSearchResult(altarTile, pos.subtract(masterPos));
            }
        }

        return new AltarSearchResult(null, null);
    }

    public record AltarSearchResult(@Nullable BloodAltarTile altar, @Nullable BlockPos offset) {}

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

    public static void writeAltarOffset(CompoundTag tag, @Nullable BlockPos offset) {
        if (offset != null) {
            tag.putInt("altarOffsetX", offset.getX());
            tag.putInt("altarOffsetY", offset.getY());
            tag.putInt("altarOffsetZ", offset.getZ());
        }
    }

    public static Map<EnumWillType, Double> queryAllWill(Level level, BlockPos pos) {
        Map<EnumWillType, Double> will = new EnumMap<>(EnumWillType.class);
        for (EnumWillType type : EnumWillType.values()) {
            will.put(type, WorldDemonWillHandler.getCurrentWill(level, pos, type));
        }
        return will;
    }

    public static void drainAllWill(Level level, BlockPos pos, Map<EnumWillType, Double> willUsed) {
        willUsed.forEach((type, amount) -> {
            if (amount > 0) {
                WorldDemonWillHandler.drainWillFromChunk(level, pos, type, amount);
            }
        });
    }

    public static void syphonLP(RitualContext context, int cost) {
        if (cost > 0) {
            int actualCost = Math.min(cost, context.currentEssence());
            context.network().syphon(context.master().ticket(actualCost));
        }
    }

    public static int getMaxOperations(RitualContext context, int costPerOperation) {
        if (costPerOperation <= 0) return Integer.MAX_VALUE;
        return context.currentEssence() / costPerOperation;
    }

    public record RitualContext(
            Level level,
            SoulNetwork network,
            int currentEssence,
            BlockPos masterPos,
            IMasterRitualStone master
    ) {
        public void syphon(int cost) {
            RitualHelper.syphonLP(this, cost);
        }

        public int maxOperations(int costPerOperation) {
            return RitualHelper.getMaxOperations(this, costPerOperation);
        }
    }
}
