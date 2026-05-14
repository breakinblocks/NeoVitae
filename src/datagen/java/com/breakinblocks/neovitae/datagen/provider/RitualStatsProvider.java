package com.breakinblocks.neovitae.datagen.provider;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.Ritual;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider that generates the ritual_stats datamap file.
 * NeoForge DataMaps require a single file per datamap type, so all rituals go in one file.
 * File is placed at data/neovitae/data_maps/neovitae/ritual/ritual_stats.json
 */
public class RitualStatsProvider implements DataProvider {
    private final PackOutput packOutput;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    private final List<RitualEntry> entries = new ArrayList<>();

    public RitualStatsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.packOutput = packOutput;
        this.lookupProvider = lookupProvider;
    }

    protected void addRituals() {
        // ==================== Essential Rituals ====================

        // Water Ritual - fast refresh, low cost
        add(NVRituals.WATER, RitualStats.timed(500, 25, 1, 0));

        // Lava Ritual - higher cost than water
        add(NVRituals.LAVA, RitualStats.timed(10000, 500, 1, 0));

        // Green Grove Ritual - plant growth
        add(NVRituals.GREEN_GROVE, RitualStats.timed(1000, 20, 20, 0));

        // Well of Suffering - mob damage for LP
        add(NVRituals.WELL_OF_SUFFERING, RitualStats.timed(50000, 2, 20, 0));

        // Feathered Knife - player HP for LP
        add(NVRituals.FEATHERED_KNIFE, RitualStats.timed(25000, 20, 20, 0));

        // Harvest Ritual
        add(NVRituals.HARVEST, RitualStats.timed(20000, 20, 20, 0));

        // ==================== Common Rituals ====================

        add(NVRituals.REGENERATION, RitualStats.timed(25000, 100, 20, 0));
        add(NVRituals.SPEED, RitualStats.timed(500, 25, 60, 0));
        add(NVRituals.JUMPING, RitualStats.timed(500, 25, 60, 0));
        add(NVRituals.MAGNETISM, RitualStats.timed(5000, 50, 20, 0));
        add(NVRituals.ANIMAL_GROWTH, RitualStats.timed(10000, 25, 20, 0));
        add(NVRituals.CRUSHING, RitualStats.timed(2500, 100, 40, 0));
        add(NVRituals.FELLING, RitualStats.timed(20000, 20, 20, 0));
        add(NVRituals.SUPPRESSION, RitualStats.timed(10000, 2, 1, 0));
        add(NVRituals.CONTAINMENT, RitualStats.timed(2000, 1, 20, 0));
        add(NVRituals.EXPULSION, RitualStats.timed(1000, 1, 20, 0));
        add(NVRituals.ZEPHYR, RitualStats.timed(1000, 1, 20, 0));
        add(NVRituals.PUMP, RitualStats.timed(10000, 25, 20, 0));

        // ==================== Advanced Rituals ====================

        add(NVRituals.PHANTOM_BRIDGE, RitualStats.timed(5000, 10, 1, 0));
        add(NVRituals.CRYSTALLUM_FRACTURA, RitualStats.timed(100000, 200, 100, 1));
        add(NVRituals.DOWNGRADE, RitualStats.timed(20000, 100, 20, 1));

        // Meteor Ritual - very expensive, one-time cost
        add(NVRituals.METEOR, RitualStats.timed(1000000, 1000000, 20, 2));

        add(NVRituals.FORSAKEN_SOUL, RitualStats.timed(50000, 1, 20, 1));
        add(NVRituals.FULL_STOMACH, RitualStats.timed(100000, 100, 20, 1));

        // ==================== Dusk Tier Rituals ====================

        add(NVRituals.CONDOR, RitualStats.timed(1000000, 200, 20, 2));
        add(NVRituals.ELLIPSE, RitualStats.timed(10000, 200, 1, 2));
        add(NVRituals.SPHERE, RitualStats.timed(10000, 200, 1, 2));
        add(NVRituals.ARMOUR_EVOLVE, RitualStats.timed(50000, 10, 1, 2));
        add(NVRituals.UPGRADE_REMOVE, RitualStats.timed(20000, 100, 20, 2));
        add(NVRituals.CRAFTING, RitualStats.timed(25000, 100, 40, 2));
        add(NVRituals.YAWNING_VOID, RitualStats.timed(500000, 50, 1, 2));

        // ==================== Utility Rituals ====================

        add(NVRituals.PLACER, RitualStats.timed(5000, 10, 20, 0));
        add(NVRituals.GROUNDING, RitualStats.timed(1000, 10, 20, 0));

        add(NVRituals.TORMENT_NEXUS, RitualStats.timed(25000, 0, 20, 2));
    }

    protected void add(DeferredHolder<Ritual, ? extends Ritual> ritual, RitualStats stats) {
        Identifier ritualId = ritual.getId();
        entries.add(new RitualEntry(ritualId, stats));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        entries.clear();
        addRituals();

        return lookupProvider.thenCompose(provider -> {
            // NeoForge DataMaps require a single file per datamap type
            // Build a single JSON with all ritual stats
            JsonObject values = new JsonObject();
            for (RitualEntry entry : entries) {
                JsonObject statsJson = serializeStats(entry.stats());
                values.add(entry.ritualId().toString(), statsJson);
            }

            JsonObject root = new JsonObject();
            root.add("values", values);

            // Output to data/neovitae/data_maps/neovitae/ritual/ritual_stats.json
            Path path = packOutput.getOutputFolder()
                    .resolve("data")
                    .resolve(NeoVitae.MODID)
                    .resolve("data_maps")
                    .resolve(NeoVitae.MODID)
                    .resolve("ritual")
                    .resolve("ritual_stats.json");

            return DataProvider.saveStable(output, root, path);
        });
    }

    private JsonObject serializeStats(RitualStats stats) {
        // Create the stats object
        JsonObject statsJson = new JsonObject();
        statsJson.addProperty("activation_cost", stats.activationCost());
        statsJson.addProperty("refresh_cost", stats.refreshCost());

        if (stats.refreshTime() != 20) {
            statsJson.addProperty("refresh_time", stats.refreshTime());
        }

        if (stats.crystalLevel() != 0) {
            statsJson.addProperty("crystal_level", stats.crystalLevel());
        }

        // Range limits are optional and typically empty for basic stats
        if (!stats.rangeLimits().isEmpty()) {
            JsonObject rangeLimitsJson = new JsonObject();
            stats.rangeLimits().forEach((name, limit) -> {
                JsonObject limitJson = new JsonObject();
                if (limit.maxVolume() != Integer.MAX_VALUE) {
                    limitJson.addProperty("max_volume", limit.maxVolume());
                }
                if (limit.maxHorizontalRadius() != 256) {
                    limitJson.addProperty("max_horizontal_radius", limit.maxHorizontalRadius());
                }
                if (limit.maxVerticalRadius() != 256) {
                    limitJson.addProperty("max_vertical_radius", limit.maxVerticalRadius());
                }
                if (limitJson.size() > 0) {
                    rangeLimitsJson.add(name, limitJson);
                }
            });
            if (rangeLimitsJson.size() > 0) {
                statsJson.add("range_limits", rangeLimitsJson);
            }
        }

        return statsJson;
    }

    @Override
    public String getName() {
        return "NeoVitae Ritual Stats";
    }

    private record RitualEntry(Identifier ritualId, RitualStats stats) {}
}
