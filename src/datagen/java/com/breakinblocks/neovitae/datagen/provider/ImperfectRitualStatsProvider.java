package com.breakinblocks.neovitae.datagen.provider;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datamap.ImperfectRitualStats;
import com.breakinblocks.neovitae.ritual.BMRituals;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider that generates the imperfect_ritual_stats datamap file.
 * NeoForge DataMaps require a single file per datamap type, so all rituals go in one file.
 * File is placed at data/bloodmagic/data_maps/bloodmagic/imperfect_ritual/imperfect_ritual_stats.json
 */
public class ImperfectRitualStatsProvider implements DataProvider {
    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final List<ImperfectRitualEntry> entries = new ArrayList<>();

    public ImperfectRitualStatsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.packOutput = packOutput;
        this.lookupProvider = lookupProvider;
    }

    protected void addImperfectRituals() {
        // Make it Rain (Thunderstorm) - Water Source, 5000 LP, no lightning effect, does not consume block
        add(BMRituals.IMPERFECT_RAIN, ImperfectRitualStats.simpleQuiet(5000, Blocks.WATER));

        // Strong Zombie - Coal Block, 5000 LP, does not consume block
        add(BMRituals.IMPERFECT_ZOMBIE, ImperfectRitualStats.simple(5000, Blocks.COAL_BLOCK));

        // Fire Resistance - Bedrock, 5000 LP, no lightning effect (used in nether), does not consume block
        add(BMRituals.IMPERFECT_RESISTANCE, ImperfectRitualStats.simpleQuiet(5000, Blocks.BEDROCK));
    }

    protected void add(DeferredHolder<ImperfectRitual, ? extends ImperfectRitual> ritual, ImperfectRitualStats stats) {
        ResourceLocation ritualId = ritual.getId();
        entries.add(new ImperfectRitualEntry(ritualId, stats));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        entries.clear();
        addImperfectRituals();

        return lookupProvider.thenCompose(provider -> {
            // NeoForge DataMaps require a single file per datamap type
            // Build a single JSON with all ritual stats
            JsonObject values = new JsonObject();
            for (ImperfectRitualEntry entry : entries) {
                JsonObject statsJson = serializeStats(entry.stats());
                values.add(entry.ritualId().toString(), statsJson);
            }

            JsonObject root = new JsonObject();
            root.add("values", values);

            // Output to data/bloodmagic/data_maps/bloodmagic/imperfect_ritual/imperfect_ritual_stats.json
            Path path = packOutput.getOutputFolder()
                    .resolve("data")
                    .resolve(NeoVitae.MODID)
                    .resolve("data_maps")
                    .resolve(NeoVitae.MODID)
                    .resolve("imperfect_ritual")
                    .resolve("imperfect_ritual_stats.json");

            return DataProvider.saveStable(output, root, path);
        });
    }

    private JsonObject serializeStats(ImperfectRitualStats stats) {
        JsonObject statsJson = new JsonObject();
        statsJson.addProperty("activation_cost", stats.activationCost());

        // Block or block tag
        if (stats.block().isPresent()) {
            Block block = stats.block().get();
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            statsJson.addProperty("block", blockId.toString());
        }

        if (stats.blockTag().isPresent()) {
            statsJson.addProperty("block_tag", stats.blockTag().get().location().toString());
        }

        // Always include consume_block for clarity
        statsJson.addProperty("consume_block", stats.consumeBlock());

        // Only include lightning_effect if false (default is true)
        if (!stats.lightningEffect()) {
            statsJson.addProperty("lightning_effect", false);
        }

        return statsJson;
    }

    @Override
    public String getName() {
        return "NeoVitae Imperfect Ritual Stats";
    }

    private record ImperfectRitualEntry(ResourceLocation ritualId, ImperfectRitualStats stats) {}
}
