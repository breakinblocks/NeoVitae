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
import com.breakinblocks.neovitae.datagen.content.SentientUpgrades;
import com.breakinblocks.neovitae.datagen.content.SigilTypes;
import com.breakinblocks.neovitae.datagen.provider.*;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import com.klikli_dev.modonomicon.api.datagen.BookProvider;
import com.breakinblocks.neovitae.datagen.book.NVBookProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        var langProvider = new NVLanguageProvider(output);

        event.createDatapackRegistryObjects(new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, NVDamageSourcesContent::bootstrap)
            .add(NVRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::bootstrap)
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
        // and the 218 multiblock definitions live as static JSON under src/main/resources/data/
        // neovitae/ since neither provider can regenerate faithfully in 26.1 (advancement datagen
        // was rewritten; multiblock builder depended on removed ItemStack construction paths).
        generator.addProvider(true, new BookProvider(
                output, provider, NeoVitae.MODID,
                List.of(new NVBookProvider(langProvider))
        ));
        generator.addProvider(true, langProvider);
    }
}
