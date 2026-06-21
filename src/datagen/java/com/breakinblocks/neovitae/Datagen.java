package com.breakinblocks.neovitae;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.datagen.content.AltarTiers;
import com.breakinblocks.neovitae.datagen.content.NVDamageSourcesContent;
import com.breakinblocks.neovitae.datagen.content.RitualLayoutsContent;
import com.breakinblocks.neovitae.datagen.content.SentientUpgrades;
import com.breakinblocks.neovitae.datagen.content.SigilTypes;
import com.breakinblocks.neovitae.datagen.provider.*;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.api.datagen.NeoResearchProvider;
import com.klikli_dev.modonomicon.api.datagen.research.ResearchCache;
import com.breakinblocks.neovitae.datagen.book.NVBookProvider;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        event.createDatapackRegistryObjects(new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, NVDamageSourcesContent::bootstrap)
            .add(NVRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::bootstrap)
            .add(NVRegistries.Keys.RITUAL_LAYOUT_KEY, RitualLayoutsContent::bootstrap)
            .add(NVRegistries.Keys.SENTIENT_UPGRADES, SentientUpgrades::bootstrap)
            .add(SigilTypeRegistry.SIGIL_TYPE_KEY, SigilTypes::bootstrap)
        );

        ProviderHelper helper = new ProviderHelper();

        event.createProvider(helper.tagsFor(NVRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::tags));
        event.createProvider(helper.tagsFor(NVRegistries.Keys.SENTIENT_UPGRADES, SentientUpgrades::tags));
        event.createProvider(helper.tagsFor(Registries.DAMAGE_TYPE, NVDamageSourcesContent::tags));
        event.createBlockAndItemTags(NVBlockTagProvider::new, NVItemTagProvider::new);

        // Model providers (blockstates + block/item models) are not registered — the pre-baked
        // JSON under src/generated/resources/assets/neovitae/{blockstates,models}/ + the 497
        // hand-authored selectors under src/main/resources/assets/neovitae/items/ are the
        // authoritative source in 26.1. Registering an empty ModelProvider here would cause
        // HashCache to delete every file tracked under its cache hash.
        event.createProvider(NVFluidTagProvider::new);
        event.createProvider(NVEntityTagProvider::new);
        event.createProvider(NVSpriteSourceProvider::new);
        generator.addProvider(true, new DungeonRoomProvider(output));

        event.createProvider(NVDataMapProvider::new);
        event.createProvider(SigilStatsProvider::new);
        event.createProvider(RitualStatsProvider::new);
        event.createProvider(ImperfectRitualStatsProvider::new);
        event.createProvider(NVLootTableProvider::new);
        event.createProvider(NVRecipeProvider.Runner::new);

        // NVAdvancementProvider + NVModonomiconMultiblockProvider: the custom advancement chain
        // lives as static JSON under src/main/resources/data/neovitae/ (26.1 rewrote the
        // advancement datagen API). Altar/ritual multiblock previews are derived at runtime
        // from the AltarTier / ritual_layout datapack registries by NVAltarBookSync and
        // NVRitualBookSync, so there's no datagen provider for them either.
        var langCache = new LanguageProviderCache("en_us");
        var researchCache = new ResearchCache();

        // Important: Lang provider (in this case en_us) needs to be added after the book provider
        // to process the texts added by the book provider
        generator.addProvider(true, NeoBookProvider.of(event, langCache, researchCache,
                new NVBookProvider()
        ));
        // Required for entryViewedOnce(): the book provider queues generated research into
        // researchCache, and the research provider writes those generated nodes/hooks/facts.
        generator.addProvider(true, NeoResearchProvider.of(event, langCache, researchCache));
        generator.addProvider(true, new NVLanguageProvider(output, langCache));
    }
}
