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
import com.breakinblocks.neovitae.common.registry.NVRegistries;
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

        generator.addProvider(event.includeClient(), new NVItemModelProvider(output, fileHelper));
        generator.addProvider(event.includeClient(), new NVBlockStateProvider(output, fileHelper));

        event.createProvider(NVLanguageProvider::new);

        event.createDatapackRegistryObjects(new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, BloodyDamageSources::bootstrap)
            .add(NVRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::bootstrap)
            .add(NVRegistries.Keys.LIVING_UPGRADES, LivingUpgrades::bootstrap)
            .add(SigilTypeRegistry.SIGIL_TYPE_KEY, SigilTypes::bootstrap)
        );

        ProviderHelper helper = new ProviderHelper(fileHelper);

        event.createProvider(helper.tagsFor(NVRegistries.Keys.ALTAR_TIER_KEY, AltarTiers::tags));
        event.createProvider(helper.tagsFor(NVRegistries.Keys.LIVING_UPGRADES, LivingUpgrades::tags));
        event.createProvider(helper.tagsFor(Registries.DAMAGE_TYPE, BloodyDamageSources::tags));
        event.createBlockAndItemTags(NVBlockTagProvider::new, NVItemTagProvider::new);

        // Fluid and entity tags
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new NVFluidTagProvider(output, provider, fileHelper));
        generator.addProvider(event.includeServer(), new NVEntityTagProvider(output, provider, fileHelper));

        // Sprite sources for custom model textures
        generator.addProvider(event.includeClient(), new NVSpriteSourceProvider(output, provider, fileHelper));

        // Dungeon room definitions
        generator.addProvider(event.includeServer(), new DungeonRoomProvider(output));

        event.createProvider(NVDataMapProvider::new);
        event.createProvider(SigilStatsProvider::new);
        event.createProvider(RitualStatsProvider::new);
        event.createProvider(ImperfectRitualStatsProvider::new);

        event.createProvider(NVLootTableProvider::new);

        event.createProvider(NVRecipeProvider::new);
    }
}
