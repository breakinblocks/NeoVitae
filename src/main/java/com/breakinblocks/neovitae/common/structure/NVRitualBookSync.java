package com.breakinblocks.neovitae.common.structure;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import com.klikli_dev.modonomicon.data.MultiblockDataManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mirror of {@link NVAltarBookSync} for rituals. For every registered ritual,
 * builds a Modonomicon multiblock from its effective layout (datapack override
 * or hardcoded default) and installs it under {@code neovitae:ritual/<path>}
 * before Modonomicon's sync packet is sent. Packs that ship a custom
 * Modonomicon multiblock JSON at that id keep it (we never overwrite an
 * existing entry).
 */
public final class NVRitualBookSync {

    private static final Set<ResourceLocation> INJECTED_IDS = new HashSet<>();

    private NVRitualBookSync() {
    }

    public static void register(IEventBus bus) {
        bus.addListener(NVRitualBookSync::onServerStarted);
        bus.addListener(EventPriority.HIGHEST, NVRitualBookSync::onDatapackSync);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        installRitualMultiblocks(event.getServer());
    }

    private static void onDatapackSync(OnDatapackSyncEvent event) {
        installRitualMultiblocks(event.getPlayerList().getServer());
    }

    private static void installRitualMultiblocks(MinecraftServer server) {
        if (server == null) return;
        if (!ModList.get().isLoaded("modonomicon")) return;

        MultiblockDataManager manager = MultiblockDataManager.get();
        Map<ResourceLocation, Multiblock> multiblocks = manager.getMultiblocks();

        for (ResourceLocation id : INJECTED_IDS) {
            multiblocks.remove(id);
        }
        INJECTED_IDS.clear();

        RegistryAccess registries = server.registryAccess();

        for (Ritual ritual : RitualRegistry.getAllRituals()) {
            ResourceLocation ritualId = RitualRegistry.getId(ritual);
            if (ritualId == null) continue;
            ResourceLocation multiblockId = NeoVitae.rl("ritual/" + ritualId.getPath());

            if (multiblocks.containsKey(multiblockId)) continue;

            List<RitualComponent> components = RitualLayouts.get(registries, ritual);
            if (components.isEmpty()) continue;

            try {
                Multiblock mb = RitualMultiblockBuilder.build(components, registries);
                mb.setId(multiblockId);
                multiblocks.put(multiblockId, mb);
                INJECTED_IDS.add(multiblockId);
            } catch (Throwable t) {
                NeoVitae.LOGGER.error("Failed to build ritual multiblock for {}", ritualId, t);
            }
        }
    }
}
