package com.breakinblocks.neovitae.datagen.provider;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datamap.SigilStats;
import com.breakinblocks.neovitae.common.item.BMItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Data provider that generates the sigil_stats datamap file.
 * NeoForge DataMaps require a single file per datamap type, so all sigils go in one file.
 * File is placed at data/bloodmagic/data_maps/item/sigil_stats.json
 */
public class SigilStatsProvider implements DataProvider {
    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final List<SigilEntry> entries = new ArrayList<>();

    public SigilStatsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.packOutput = packOutput;
        this.lookupProvider = lookupProvider;
    }

    protected void addSigils() {
        // Divination sigils - no LP cost (info only)
        add(BMItems.SIGIL_DIVINATION, SigilStats.simple(0));
        add(BMItems.SIGIL_SEER, SigilStats.simple(0));

        // Fluid sigils - single-use activation cost
        add(BMItems.SIGIL_WATER, SigilStats.simple(100));
        add(BMItems.SIGIL_LAVA, SigilStats.simple(1000));
        add(BMItems.SIGIL_VOID, SigilStats.simple(50));

        // Movement sigils
        add(BMItems.SIGIL_AIR, SigilStats.simple(50));

        // Utility sigils
        add(BMItems.SIGIL_BLOOD_LIGHT, SigilStats.simple(10));
        add(BMItems.SIGIL_TELEPOSITION, SigilStats.simple(1000));

        // Toggleable sigils with ranges
        // Magnetism: range 5, vertical range 5
        add(BMItems.SIGIL_MAGNETISM, SigilStats.ranged(50, 5, 5));

        // Fast Miner: LP per tick, array effect with radius 10, duration 600 ticks (30 sec), level 2
        add(BMItems.SIGIL_FAST_MINER, SigilStats.full(100, 100, 10, 10, 600, 2));

        // Green Grove: range 3, vertical range 2
        add(BMItems.SIGIL_GREEN_GROVE, SigilStats.ranged(150, 3, 2));

        // Frost: horizontal radius 2
        add(BMItems.SIGIL_FROST, SigilStats.ranged(100, 2, 2));

        // Suppression: radius 5 horizontal, 5 vertical
        add(BMItems.SIGIL_SUPPRESSION, SigilStats.ranged(400, 5, 5));

        // Holding: no LP cost (container only)
        add(BMItems.SIGIL_HOLDING, SigilStats.simple(0));
    }

    protected void add(Supplier<? extends Item> item, SigilStats stats) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.get());
        entries.add(new SigilEntry(itemId, stats));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        entries.clear();
        addSigils();

        return lookupProvider.thenCompose(provider -> {
            // NeoForge DataMaps require a single file per datamap type
            // Build a single JSON with all sigil stats
            JsonObject values = new JsonObject();
            for (SigilEntry entry : entries) {
                JsonObject statsJson = serializeStats(entry.stats());
                values.add(entry.itemId().toString(), statsJson);
            }

            JsonObject root = new JsonObject();
            root.add("values", values);

            // Output to data/bloodmagic/data_maps/item/sigil_stats.json
            Path path = packOutput.getOutputFolder()
                    .resolve("data")
                    .resolve(NeoVitae.MODID)
                    .resolve("data_maps")
                    .resolve("item")
                    .resolve("sigil_stats.json");

            return DataProvider.saveStable(output, root, path);
        });
    }

    private JsonObject serializeStats(SigilStats stats) {
        // Create the stats object
        JsonObject statsJson = new JsonObject();
        statsJson.addProperty("lp_cost", stats.lpCost());

        if (stats.drainInterval() != SigilStats.DEFAULT_DRAIN_INTERVAL) {
            statsJson.addProperty("drain_interval", stats.drainInterval());
        }

        stats.range().ifPresent(range -> statsJson.addProperty("range", range));
        stats.verticalRange().ifPresent(vRange -> statsJson.addProperty("vertical_range", vRange));
        stats.effectDuration().ifPresent(duration -> statsJson.addProperty("effect_duration", duration));
        stats.effectLevel().ifPresent(level -> statsJson.addProperty("effect_level", level));

        return statsJson;
    }

    @Override
    public String getName() {
        return "NeoVitae Sigil Stats";
    }

    private record SigilEntry(ResourceLocation itemId, SigilStats stats) {}
}
