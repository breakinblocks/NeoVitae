package com.breakinblocks.neovitae.common.structure;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Generates Modonomicon multiblock entries for each loaded altar tier so the
 * in-book preview matches the datapack-defined layout. A pack that ships its
 * own Modonomicon multiblock JSON for a given altar id wins; we only fill in
 * ids that were not provided on disk.
 *
 * Runs at ServerStartedEvent (initial load) and at HIGHEST priority on
 * OnDatapackSyncEvent (so the multiblocks are present before Modonomicon's
 * sync packet is built).
 */
public final class NVAltarBookSync {

    private static final String TIER_NAME_PREFIX = "altar_";
    private static final String[] TIER_NAMES = {"one", "two", "three", "four", "five", "six"};
    private static final Set<ResourceLocation> INJECTED_IDS = new HashSet<>();

    private NVAltarBookSync() {
    }

    public static void register(IEventBus bus) {
        bus.addListener(NVAltarBookSync::onServerStarted);
        bus.addListener(EventPriority.HIGHEST, NVAltarBookSync::onDatapackSync);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        installAltarMultiblocks(event.getServer());
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        installAltarMultiblocks(event.getPlayerList().getServer());
    }

    private static void installAltarMultiblocks(MinecraftServer server) {
        if (server == null) return;
        if (!ModList.get().isLoaded("modonomicon")) return;

        MultiblockDataManager manager = MultiblockDataManager.get();
        Map<ResourceLocation, Multiblock> multiblocks = manager.getMultiblocks();

        for (ResourceLocation id : INJECTED_IDS) {
            multiblocks.remove(id);
        }
        INJECTED_IDS.clear();

        RegistryAccess registries = server.registryAccess();
        var tierRegistry = registries.registryOrThrow(NVRegistries.Keys.ALTAR_TIER_KEY);
        var validTiers = tierRegistry.getOrCreateTag(NVTags.Tiers.VALID_TIERS);

        for (Holder<AltarTier> holder : validTiers) {
            AltarTier tier = holder.value();
            int idx = tier.tier();
            if (idx < 0 || idx >= TIER_NAMES.length) continue;
            ResourceLocation multiblockId = NeoVitae.rl(TIER_NAME_PREFIX + TIER_NAMES[idx]);

            if (multiblocks.containsKey(multiblockId)) continue;

            try {
                Multiblock mb = AltarMultiblockBuilder.build(tier, registries);
                mb.setId(multiblockId);
                multiblocks.put(multiblockId, mb);
                INJECTED_IDS.add(multiblockId);
            } catch (Throwable t) {
                NeoVitae.LOGGER.error("Failed to build altar multiblock for tier {} ({})", idx, multiblockId, t);
            }
        }
    }
}
