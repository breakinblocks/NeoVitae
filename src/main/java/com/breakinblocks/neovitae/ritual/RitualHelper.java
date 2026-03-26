package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.api.will.SpiritusHandler;
import com.breakinblocks.neovitae.api.will.SpiritusState;
import com.breakinblocks.neovitae.common.datacomponent.Anima;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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

        Anima network = masterRitualStone.getOwnerNetwork();
        if (network == null) {
            return null;
        }

        int currentEV = network.getCurrentEV();
        if (currentEV < minEssence) {
            return null;
        }

        return new RitualContext(level, network, currentEV, masterRitualStone.getBlockPos(), masterRitualStone);
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

    public static List<LivingEntity> getAliveLivingEntities(RitualContext context, Ritual ritual, String rangeKey) {
        return getEntitiesInRange(context, ritual, rangeKey, LivingEntity.class, LivingEntity::isAlive);
    }

    public static List<LivingEntity> getAliveMobsInRange(RitualContext context, Ritual ritual, String rangeKey) {
        return getEntitiesInRange(context, ritual, rangeKey, LivingEntity.class,
                entity -> entity.isAlive() && !(entity instanceof Player));
    }

    public static List<Player> getAlivePlayersInRange(RitualContext context, Ritual ritual, String rangeKey) {
        return getEntitiesInRange(context, ritual, rangeKey, Player.class,
                player -> player.isAlive() && !player.isSpectator());
    }

    /**
     * Finds a AraVitaeTile within a ritual's range, using a cached offset if available.
     */
    public static AltarSearchResult findAltar(RitualContext context, Ritual ritual,
            String rangeKey, @Nullable BlockPos cachedOffset) {
        BlockPos masterPos = context.masterPos();

        if (cachedOffset != null) {
            BlockPos altarPos = masterPos.offset(cachedOffset);
            BlockEntity be = context.level().getBlockEntity(altarPos);
            if (be instanceof AraVitaeTile altarTile) {
                return new AltarSearchResult(altarTile, cachedOffset);
            }
        }

        List<BlockPos> positions = getRangePositions(context.master(), ritual, rangeKey, masterPos);
        for (BlockPos pos : positions) {
            BlockEntity be = context.level().getBlockEntity(pos);
            if (be instanceof AraVitaeTile altarTile) {
                return new AltarSearchResult(altarTile, pos.subtract(masterPos));
            }
        }

        return new AltarSearchResult(null, null);
    }

    public record AltarSearchResult(@Nullable AraVitaeTile altar, @Nullable BlockPos offset) {}

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

    /**
     * Queries all spiritus types for a chunk and returns a snapshot with threshold checks.
     * Convenience method delegating to {@link SpiritusHandler#queryWill(Level, BlockPos, double)}.
     */
    public static SpiritusState queryWill(Level level, BlockPos pos, double threshold) {
        return SpiritusHandler.INSTANCE.queryWill(level, pos, threshold);
    }

    /**
     * Creates a netherite pickaxe with optional Fortune or Silk Touch enchantment.
     * Used by block-breaking rituals for loot table context.
     */
    public static ItemStack createMiningTool(ServerLevel level, boolean fortune, boolean silkTouch) {
        ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
        if (fortune) {
            Holder<Enchantment> ench = level.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            tool.enchant(ench, 3);
        } else if (silkTouch) {
            Holder<Enchantment> ench = level.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
            tool.enchant(ench, 1);
        }
        return tool;
    }

    public static void syphonEV(RitualContext context, int cost) {
        if (cost > 0) {
            int actualCost = Math.min(cost, context.currentEV());
            context.network().syphon(context.master().ticket(actualCost));
        }
    }

    public static int getMaxOperations(RitualContext context, int costPerOperation) {
        if (costPerOperation <= 0) return Integer.MAX_VALUE;
        return context.currentEV() / costPerOperation;
    }

    public record RitualContext(
            Level level,
            Anima network,
            int currentEV,
            BlockPos masterPos,
            IMasterRitualStone master
    ) {
        public void syphon(int cost) {
            RitualHelper.syphonEV(this, cost);
        }

        public int maxOperations(int costPerOperation) {
            return RitualHelper.getMaxOperations(this, costPerOperation);
        }
    }
}
