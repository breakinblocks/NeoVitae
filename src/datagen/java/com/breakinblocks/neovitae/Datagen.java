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
import com.breakinblocks.neovitae.datagen.content.LivingUpgrades;
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
            .add(NVRegistries.Keys.LIVING_UPGRADES, LivingUpgrades::bootstrap)
            .add(SigilTypeRegistry.SIGIL_TYPE_KEY, SigilTypes::bootstrap)
        );

        ProviderHelper helper = new ProviderHelper();

        event.createProvider(helper.tagsFor(NVRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::tags));
        event.createProvider(helper.tagsFor(NVRegistries.Keys.LIVING_UPGRADES, LivingUpgrades::tags));
        event.createProvider(helper.tagsFor(Registries.DAMAGE_TYPE, NVDamageSourcesContent::tags));
        event.createBlockAndItemTags(NVBlockTagProvider::new, NVItemTagProvider::new);

        event.createProvider(NVItemModelProvider::new);
        event.createProvider(NVBlockStateProvider::new);
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

        event.createProvider(NVAdvancementProvider::new);
        generator.addProvider(true, new NVModonomiconMultiblockProvider(output));
        generator.addProvider(true, new BookProvider(
                output, provider, NeoVitae.MODID,
                List.of(new NVBookProvider(langProvider))
        ));
        generator.addProvider(true, langProvider);
    }
}
