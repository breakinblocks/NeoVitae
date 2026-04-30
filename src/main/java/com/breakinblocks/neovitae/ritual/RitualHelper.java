package com.breakinblocks.neovitae.ritual;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.api.will.SpiritusHandler;
import com.breakinblocks.neovitae.api.will.SpiritusState;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.util.Utils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.Optional;
import java.util.function.Predicate;

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

    /**
     * Returns the first block position in a ritual's configured range, or
     * {@link Optional#empty()} if the range is missing or empty. Callers that
     * only care about a single anchor position (chest slot, altar lookup, etc.)
     * should prefer this over {@code getRangePositions(...).getFirst()} so a
     * user-configured empty range does not throw {@link java.util.NoSuchElementException}.
     */
    public static Optional<BlockPos> firstPositionInRange(IMasterRitualStone masterRitualStone, Ritual ritual,
            String rangeKey, BlockPos masterPos) {
        List<BlockPos> positions = getRangePositions(masterRitualStone, ritual, rangeKey, masterPos);
        return positions.isEmpty() ? Optional.empty() : Optional.of(positions.getFirst());
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
            String rangeKey, Class<T> entityClass, Predicate<T> filter) {
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
                    tag.getIntOr("altarOffsetX", 0),
                    tag.getIntOr("altarOffsetY", 0),
                    tag.getIntOr("altarOffsetZ", 0)
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

    /**
     * Creates a fake player for ritual loot-context and block-break operations.
     * The display name is bracketed so the entity is obvious in logs and
     * protection mod allowlists.
     */
    public static FakePlayer createRitualFakePlayer(ServerLevel level, UUID owner, String tag) {
        return new FakePlayer(level, new GameProfile(owner, "[" + tag + "]"));
    }

    /**
     * Resolves the chest output at the first position in the given range. The
     * returned {@link ChestOutput} aggregates the block entity, the downward
     * inventory view, and a convenience {@code hasFreeSlot} flag so block-break
     * rituals can avoid re-fetching the capability per drop.
     */
    public static ChestOutput resolveChestOutput(RitualContext context, Ritual ritual, String rangeKey) {
        BlockPos chestPos = firstPositionInRange(context.master(), ritual, rangeKey, context.masterPos()).orElse(null);
        if (chestPos == null) return ChestOutput.EMPTY;
        BlockEntity tile = context.level().getBlockEntity(chestPos);
        if (tile == null) return ChestOutput.EMPTY;
        boolean hasFreeSlot = Utils.getNumberOfFreeSlots(tile, Direction.DOWN) >= 1;
        return new ChestOutput(tile, hasFreeSlot);
    }

    /**
     * Bundle returned by {@link #resolveChestOutput} — carries the chest block
     * entity (or {@code null} when no chest is configured/found) along with a
     * pre-computed free-slot flag so callers can short-circuit without hitting
     * the capability again.
     */
    public record ChestOutput(@Nullable BlockEntity tile, boolean hasFreeSlot) {
        public static final ChestOutput EMPTY = new ChestOutput(null, false);
    }

    /**
     * Builds the standard block-break loot context used by quarry/harvest
     * rituals and returns the resulting drops. Every caller passes an
     * identical set of parameters, so this centralises the shape and keeps
     * the ritual bodies readable.
     */
    public static List<ItemStack> getBlockDrops(ServerLevel level, BlockState state, BlockPos pos,
                                                ItemStack tool, FakePlayer fakePlayer) {
        LootParams.Builder builder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, fakePlayer);
        return state.getDrops(builder);
    }

    /**
     * Distributes a list of block drops into an optional chest inventory,
     * sending any overflow through the supplied {@link Consumer}. Passing
     * {@code null} for {@code chestTile} skips insertion entirely and routes
     * every drop to the overflow sink.
     *
     * <p>Callers pick their own overflow strategy via the lambda — typically
     * {@code stack -> Block.popResource(level, pos, stack)} for drops that
     * should fall at the broken block, or
     * {@code stack -> Utils.spawnStackAtBlock(level, masterPos, Direction.UP, stack)}
     * for drops that should pop out the top of the ritual stone.
     */
    public static void distributeDrops(List<ItemStack> drops, @Nullable BlockEntity chestTile,
                                       Consumer<ItemStack> overflowSink) {
        for (ItemStack stack : drops) {
            ItemStack remaining = stack;
            if (chestTile != null) {
                remaining = Utils.insertStackIntoTile(remaining, chestTile, Direction.DOWN);
            }
            if (!remaining.isEmpty()) {
                overflowSink.accept(remaining);
            }
        }
    }

    /**
     * Applies accumulated per-type will usage to the {@link SpiritusState} and
     * commits the result to the chunk in a single call, replacing the
     * 5-line {@code will.use(...) + will.drain(...)} boilerplate that
     * block-break and buff rituals repeat at the bottom of their tick.
     *
     * <p>Arguments are positional in canonical spiritus order (raw,
     * corrosive, destructive, vengeful, steadfast); zero values are skipped
     * so callers don't pay the map insertion for an unused type.
     */
    public static void drainSpiritus(SpiritusState will, Level level, BlockPos pos,
                                 double raw, double corrosive, double destructive,
                                 double vengeful, double steadfast) {
        if (raw > 0) will.use(SpiritusType.RAW, raw);
        if (corrosive > 0) will.use(SpiritusType.RUINA, corrosive);
        if (destructive > 0) will.use(SpiritusType.NIHILUM, destructive);
        if (vengeful > 0) will.use(SpiritusType.VINDICTA, vengeful);
        if (steadfast > 0) will.use(SpiritusType.INVICTUS, steadfast);
        will.drain(level, pos);
    }

    /**
     * Chance-gated emission hook used by ritual visual effect calls. Fires the
     * supplied {@link Runnable} on a 1-in-{@code oneInN} roll, and only on the
     * server side. Wrapping every ritual stream/particle emission in this
     * helper keeps the random gate out of the ritual body and gives a single
     * seam for a future subtle-effects config toggle.
     */
    public static void chanceStream(Level level, int oneInN, Runnable emit) {
        if (level.isClientSide()) return;
        if (oneInN <= 1 || level.getRandom().nextInt(oneInN) == 0) {
            emit.run();
        }
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

        /**
         * Returns the level as a {@link ServerLevel}. Safe to call on any
         * context produced by {@link RitualHelper#createContext}, which already
         * rejects client-side levels — on the server the only concrete
         * {@link Level} subclass is {@link ServerLevel}, so the cast cannot
         * fail for a non-null ritual context.
         */
        public ServerLevel serverLevel() {
            return (ServerLevel) level;
        }
    }
}
