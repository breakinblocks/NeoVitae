package com.breakinblocks.neovitae;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import com.breakinblocks.neovitae.common.registry.BMRegistries;
import com.breakinblocks.neovitae.datagen.content.AltarTiers;
import com.breakinblocks.neovitae.datagen.content.BloodyDamageSources;
import com.breakinblocks.neovitae.datagen.content.LivingUpgrades;
import com.breakinblocks.neovitae.datagen.content.SigilTypes;
import com.breakinblocks.neovitae.datagen.provider.*;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class Datagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new BMItemModelProvider(output, fileHelper));
        generator.addProvider(event.includeClient(), new BMBlockStateProvider(output, fileHelper));

        event.createProvider(BMLanguageProvider::new);

        event.createDatapackRegistryObjects(new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, BloodyDamageSources::bootstrap)
            .add(BMRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::bootstrap)
            .add(BMRegistries.Keys.LIVING_UPGRADES, LivingUpgrades::bootstrap)
            .add(SigilTypeRegistry.SIGIL_TYPE_KEY, SigilTypes::bootstrap)
        );

        ProviderHelper helper = new ProviderHelper(fileHelper);

        event.createProvider(helper.tagsFor(BMRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::tags));
        event.createProvider(helper.tagsFor(BMRegistries.Keys.LIVING_UPGRADES, LivingUpgrades::tags));
        event.createProvider(helper.tagsFor(Registries.DAMAGE_TYPE, BloodyDamageSources::tags));
        event.createBlockAndItemTags(BMBlockTagProvider::new, BMItemTagProvider::new);

        // Fluid and entity tags
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new BMFluidTagProvider(output, provider, fileHelper));
        generator.addProvider(event.includeServer(), new BMEntityTagProvider(output, provider, fileHelper));

        // Sprite sources for custom model textures
        generator.addProvider(event.includeClient(), new BMSpriteSourceProvider(output, provider, fileHelper));

        // Dungeon room definitions
        generator.addProvider(event.includeServer(), new DungeonRoomProvider(output));

        event.createProvider(BMDataMapProvider::new);
        event.createProvider(SigilStatsProvider::new);
        event.createProvider(RitualStatsProvider::new);
        event.createProvider(ImperfectRitualStatsProvider::new);

        event.createProvider(BMLootTableProvider::new);

        event.createProvider(BMRecipeProvider::new);
    }
}
