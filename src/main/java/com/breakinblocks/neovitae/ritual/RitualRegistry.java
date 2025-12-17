package com.breakinblocks.neovitae.ritual;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for Blood Magic rituals.
 * Uses NeoForge's DeferredRegister system for proper lifecycle management.
 */
public final class RitualRegistry {
    private RitualRegistry() {}

    // ========== Imperfect Ritual Lookup Cache ==========
    // Provides O(1) lookup for imperfect rituals by catalyst block.
    // Cache is built lazily on first lookup and cleared when server stops.

    /** Maps specific blocks to their imperfect ritual holder (from DataMap block field) */
    private static Map<Block, Holder<ImperfectRitual>> blockToRitualCache = null;

    /** Rituals that use block tags for matching (checked if no direct block match) */
    private static List<TagRitualEntry> tagBasedRituals = null;

    /** Rituals without DataMap stats that use predicate matching (fallback) */
    private static List<Holder<ImperfectRitual>> predicateBasedRituals = null;

    /** Entry for tag-based ritual lookup */
    private record TagRitualEntry(TagKey<Block> tag, Holder<ImperfectRitual> holder) {}

    /**
     * The registry key for rituals.
     */
    public static final ResourceKey<Registry<Ritual>> RITUAL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(NeoVitae.rl("ritual"));

    /**
     * The registry key for imperfect rituals.
     */
    public static final ResourceKey<Registry<ImperfectRitual>> IMPERFECT_RITUAL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(NeoVitae.rl("imperfect_ritual"));

    /**
     * Deferred register for rituals.
     */
    public static final DeferredRegister<Ritual> RITUALS =
            DeferredRegister.create(RITUAL_REGISTRY_KEY, NeoVitae.MODID);

    /**
     * Deferred register for imperfect rituals.
     */
    public static final DeferredRegister<ImperfectRitual> IMPERFECT_RITUALS =
            DeferredRegister.create(IMPERFECT_RITUAL_REGISTRY_KEY, NeoVitae.MODID);

    /**
     * Register all deferred registers and custom registries to the mod event bus.
     */
    public static void register(IEventBus modBus) {
        // Create the registries
        RITUALS.makeRegistry(builder -> builder.sync(true));
        IMPERFECT_RITUALS.makeRegistry(builder -> builder.sync(true));

        // Initialize ritual registrations
        BMRituals.init();

        // Register to the mod bus
        RITUALS.register(modBus);
        IMPERFECT_RITUALS.register(modBus);

        // Register server stopped event to clear the imperfect ritual cache
        NeoForge.EVENT_BUS.addListener(RitualRegistry::onServerStopped);
    }

    /**
     * Gets the ritual registry.
     */
    public static Registry<Ritual> getRitualRegistry() {
        return RITUALS.getRegistry().get();
    }

    /**
     * Gets a ritual by its registry name.
     */
    public static Ritual getRitual(ResourceLocation id) {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.get(id) : null;
    }

    /**
     * Gets a ritual by its string name.
     */
    public static Ritual getRitual(String name) {
        return getRitual(NeoVitae.rl(name));
    }

    /**
     * Gets the resource location of a ritual.
     */
    public static ResourceLocation getId(Ritual ritual) {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.getKey(ritual) : null;
    }

    /**
     * Gets all registered rituals.
     */
    public static Collection<Ritual> getAllRituals() {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.stream().toList() : Collections.emptyList();
    }

    /**
     * Gets all registered ritual IDs (ResourceLocations).
     */
    public static Collection<ResourceLocation> getRegisteredRituals() {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.keySet() : Collections.emptySet();
    }

    /**
     * Gets the imperfect ritual registry.
     */
    public static Registry<ImperfectRitual> getImperfectRitualRegistry() {
        return IMPERFECT_RITUALS.getRegistry().get();
    }

    /**
     * Gets an imperfect ritual by its registry name.
     */
    public static ImperfectRitual getImperfectRitual(ResourceLocation id) {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.get(id) : null;
    }

    /**
     * Gets all registered imperfect rituals.
     */
    public static Collection<ImperfectRitual> getAllImperfectRituals() {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.stream().toList() : Collections.emptyList();
    }

    /**
     * Gets all registered imperfect ritual IDs (ResourceLocations).
     */
    public static Collection<ResourceLocation> getRegisteredImperfectRituals() {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.keySet() : Collections.emptySet();
    }

    /**
     * Gets the resource location of an imperfect ritual.
     */
    public static ResourceLocation getId(ImperfectRitual ritual) {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.getKey(ritual) : null;
    }

    // ========== Imperfect Ritual Lookup Methods ==========

    /**
     * Finds the imperfect ritual that matches the given block state.
     * Uses O(1) lookup for block-specific rituals, with fallback to tags and predicates.
     *
     * @param aboveState The block state above the imperfect ritual stone
     * @return The matching ritual result, or null if no match
     */
    @Nullable
    public static ImperfectRitualLookupResult findRitualForBlock(BlockState aboveState) {
        ensureCacheBuilt();

        Block block = aboveState.getBlock();

        // O(1) lookup for specific block matches
        Holder<ImperfectRitual> holder = blockToRitualCache.get(block);
        if (holder != null) {
            ImperfectRitualStats stats = holder.getData(BMDataMaps.IMPERFECT_RITUAL_STATS);
            return new ImperfectRitualLookupResult(holder.value(), stats);
        }

        // Check tag-based rituals (typically very few)
        for (TagRitualEntry entry : tagBasedRituals) {
            if (aboveState.is(entry.tag())) {
                ImperfectRitualStats stats = entry.holder().getData(BMDataMaps.IMPERFECT_RITUAL_STATS);
                return new ImperfectRitualLookupResult(entry.holder().value(), stats);
            }
        }

        // Check predicate-based rituals (fallback, typically very few)
        for (Holder<ImperfectRitual> predicateHolder : predicateBasedRituals) {
            ImperfectRitual ritual = predicateHolder.value();
            if (ritual.getBlockRequirement().test(aboveState)) {
                // No DataMap stats for predicate-based rituals
                return new ImperfectRitualLookupResult(ritual, null);
            }
        }

        return null;
    }

    /**
     * Result of an imperfect ritual lookup, containing the ritual and optional stats.
     */
    public record ImperfectRitualLookupResult(ImperfectRitual ritual, @Nullable ImperfectRitualStats stats) {}

    /**
     * Builds the lookup cache if not already built.
     */
    private static void ensureCacheBuilt() {
        if (blockToRitualCache != null) {
            return; // Already built
        }

        blockToRitualCache = new HashMap<>();
        tagBasedRituals = new ArrayList<>();
        predicateBasedRituals = new ArrayList<>();

        Registry<ImperfectRitual> registry = getImperfectRitualRegistry();
        if (registry == null) {
            return;
        }

        for (ImperfectRitual ritual : registry) {
            Holder<ImperfectRitual> holder = registry.wrapAsHolder(ritual);
            ImperfectRitualStats stats = holder.getData(BMDataMaps.IMPERFECT_RITUAL_STATS);

            if (stats == null) {
                // No DataMap - use predicate-based matching
                predicateBasedRituals.add(holder);
            } else if (stats.block().isPresent()) {
                // Specific block - add to O(1) lookup map
                blockToRitualCache.put(stats.block().get(), holder);
            } else if (stats.blockTag().isPresent()) {
                // Tag-based - add to tag list
                tagBasedRituals.add(new TagRitualEntry(stats.blockTag().get(), holder));
            } else {
                // DataMap exists but no block/tag specified - unusual, treat as predicate
                predicateBasedRituals.add(holder);
            }
        }

        NeoVitae.LOGGER.debug("Built imperfect ritual cache: {} block mappings, {} tag-based, {} predicate-based",
                blockToRitualCache.size(), tagBasedRituals.size(), predicateBasedRituals.size());
    }

    /**
     * Clears the lookup cache. Called when server stops.
     */
    public static void clearCache() {
        blockToRitualCache = null;
        tagBasedRituals = null;
        predicateBasedRituals = null;
    }

    /**
     * Event handler for server stopped - clears the cache.
     */
    private static void onServerStopped(ServerStoppedEvent event) {
        clearCache();
    }
}
