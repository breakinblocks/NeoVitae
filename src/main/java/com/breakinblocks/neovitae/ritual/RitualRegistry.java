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
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final ResourceKey<Registry<Ritual>> RITUAL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(NeoVitae.rl("ritual"));

    public static final ResourceKey<Registry<ImperfectRitual>> IMPERFECT_RITUAL_REGISTRY_KEY =
            ResourceKey.createRegistryKey(NeoVitae.rl("imperfect_ritual"));

    public static final DeferredRegister<Ritual> RITUALS =
            DeferredRegister.create(RITUAL_REGISTRY_KEY, NeoVitae.MODID);

    public static final DeferredRegister<ImperfectRitual> IMPERFECT_RITUALS =
            DeferredRegister.create(IMPERFECT_RITUAL_REGISTRY_KEY, NeoVitae.MODID);

    public static void register(IEventBus modBus) {
        RITUALS.makeRegistry(builder -> builder.sync(true));
        IMPERFECT_RITUALS.makeRegistry(builder -> builder.sync(true));

        NVRituals.init();

        RITUALS.register(modBus);
        IMPERFECT_RITUALS.register(modBus);

        NeoForge.EVENT_BUS.addListener(RitualRegistry::onServerStopped);
    }

    public static Registry<Ritual> getRitualRegistry() {
        return RITUALS.getRegistry().get();
    }

    public static Ritual getRitual(ResourceLocation id) {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.get(id) : null;
    }

    public static Ritual getRitual(String name) {
        return getRitual(NeoVitae.rl(name));
    }

    public static ResourceLocation getId(Ritual ritual) {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.getKey(ritual) : null;
    }

    public static Collection<Ritual> getAllRituals() {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.stream().toList() : Collections.emptyList();
    }

    public static Collection<ResourceLocation> getRegisteredRituals() {
        Registry<Ritual> registry = RITUALS.getRegistry().get();
        return registry != null ? registry.keySet() : Collections.emptySet();
    }

    public static Registry<ImperfectRitual> getImperfectRitualRegistry() {
        return IMPERFECT_RITUALS.getRegistry().get();
    }

    public static ImperfectRitual getImperfectRitual(ResourceLocation id) {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.get(id) : null;
    }

    public static Collection<ImperfectRitual> getAllImperfectRituals() {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.stream().toList() : Collections.emptyList();
    }

    public static Collection<ResourceLocation> getRegisteredImperfectRituals() {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.keySet() : Collections.emptySet();
    }

    public static ResourceLocation getId(ImperfectRitual ritual) {
        Registry<ImperfectRitual> registry = IMPERFECT_RITUALS.getRegistry().get();
        return registry != null ? registry.getKey(ritual) : null;
    }

    /**
     * Finds the imperfect ritual matching the given block state.
     * O(1) lookup for specific blocks, with fallback to tags and predicates.
     */
    @Nullable
    public static ImperfectRitualLookupResult findRitualForBlock(BlockState aboveState) {
        ensureCacheBuilt();

        Block block = aboveState.getBlock();

        Holder<ImperfectRitual> holder = blockToRitualCache.get(block);
        if (holder != null) {
            ImperfectRitualStats stats = holder.getData(NVDataMaps.IMPERFECT_RITUAL_STATS);
            return new ImperfectRitualLookupResult(holder.value(), stats);
        }

        for (TagRitualEntry entry : tagBasedRituals) {
            if (aboveState.is(entry.tag())) {
                ImperfectRitualStats stats = entry.holder().getData(NVDataMaps.IMPERFECT_RITUAL_STATS);
                return new ImperfectRitualLookupResult(entry.holder().value(), stats);
            }
        }

        for (Holder<ImperfectRitual> predicateHolder : predicateBasedRituals) {
            ImperfectRitual ritual = predicateHolder.value();
            if (ritual.getBlockRequirement().test(aboveState)) {
                return new ImperfectRitualLookupResult(ritual, null);
            }
        }

        return null;
    }

    public record ImperfectRitualLookupResult(ImperfectRitual ritual, @Nullable ImperfectRitualStats stats) {}

    private static void ensureCacheBuilt() {
        if (blockToRitualCache != null) {
            return;
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
            ImperfectRitualStats stats = holder.getData(NVDataMaps.IMPERFECT_RITUAL_STATS);

            if (stats == null) {
                predicateBasedRituals.add(holder);
            } else if (stats.block().isPresent()) {
                blockToRitualCache.put(stats.block().get(), holder);
            } else if (stats.blockTag().isPresent()) {
                tagBasedRituals.add(new TagRitualEntry(stats.blockTag().get(), holder));
            } else {
                predicateBasedRituals.add(holder);
            }
        }

        NeoVitae.LOGGER.debug("Built imperfect ritual cache: {} block mappings, {} tag-based, {} predicate-based",
                blockToRitualCache.size(), tagBasedRituals.size(), predicateBasedRituals.size());
    }

    public static void clearCache() {
        blockToRitualCache = null;
        tagBasedRituals = null;
        predicateBasedRituals = null;
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        clearCache();
    }
}
