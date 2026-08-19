package com.breakinblocks.neovitae.datagen.provider;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.Ritual;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

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
        declare(this::add);
    }

    public static void declare(BiConsumer<DeferredHolder<Ritual, ? extends Ritual>, RitualStats> out) {
        // activation cost, refresh cost (EV/op), refresh time (ticks), crystal level

        // ==================== Essential Rituals ====================
        out.accept(NVRituals.WATER, RitualStats.timed(500, 25, 1, 0));
        out.accept(NVRituals.LAVA, RitualStats.timed(10000, 500, 10, 0));
        out.accept(NVRituals.GREEN_GROVE, RitualStats.timed(1000, 20, 20, 0)
                .withAmbientSound(NeoVitae.rl("overgrowth")));
        out.accept(NVRituals.WELL_OF_SUFFERING, RitualStats.timed(50000, 2, 20, 0));
        out.accept(NVRituals.FEATHERED_KNIFE, RitualStats.timed(25000, 20, 20, 0));
        out.accept(NVRituals.HARVEST, RitualStats.timed(20000, 3, 20, 0));

        // ==================== Common Rituals ====================
        out.accept(NVRituals.REGENERATION, RitualStats.timed(500, 50, 40, 0));
        out.accept(NVRituals.SPEED, RitualStats.timed(500, 5, 1, 0));
        out.accept(NVRituals.MAGNETISM, RitualStats.timed(5000, 50, 40, 0));
        out.accept(NVRituals.SHEPHERD, RitualStats.timed(500, 30, 20, 0));
        out.accept(NVRituals.BUTCHERING, RitualStats.timed(25000, 25, 40, 0));
        out.accept(NVRituals.FELLING, RitualStats.timed(2000, 10, 20, 0));
        out.accept(NVRituals.SUPPRESSION, RitualStats.timed(3000, 5, 10, 0));
        out.accept(NVRituals.CONTAINMENT, RitualStats.timed(2000, 5, 5, 0));
        out.accept(NVRituals.EXPULSION, RitualStats.timed(2000, 5, 5, 0));
        out.accept(NVRituals.ZEPHYR, RitualStats.timed(1000, 2, 5, 0));
        out.accept(NVRituals.PUMP, RitualStats.timed(2500, 50, 20, 0));

        // ==================== Advanced Rituals ====================
        out.accept(NVRituals.PHANTOM_BRIDGE, RitualStats.timed(2000, 1, 1, 0));
        out.accept(NVRituals.CRYSTALLUM_FRACTURA, RitualStats.timed(100000, 160, 100, 1));
        out.accept(NVRituals.DOWNGRADE, RitualStats.timed(20000, 10000, 20, 1).withPerOperation());
        out.accept(NVRituals.METEOR, RitualStats.timed(250000, 0, 20, 1));
        out.accept(NVRituals.FORSAKEN_SOUL, RitualStats.timed(40000, 100, 20, 1));
        out.accept(NVRituals.FULL_STOMACH, RitualStats.timed(1000, 100, 40, 0));

        // ==================== Dusk Tier Rituals ====================
        out.accept(NVRituals.CONDOR, RitualStats.timed(10000, 100, 20, 1));
        out.accept(NVRituals.SPHERE, RitualStats.timed(20000, 10, 1, 0));
        out.accept(NVRituals.ARMOUR_EVOLVE, RitualStats.timed(50000, 25000, 20, 1).withPerOperation());
        out.accept(NVRituals.UPGRADE_REMOVE, RitualStats.timed(20000, 10000, 20, 1).withPerOperation());
        out.accept(NVRituals.CRAFTING, RitualStats.timed(25000, 100, 40, 1));
        out.accept(NVRituals.YAWNING_VOID, RitualStats.timed(5000, 10, 10, 0));

        // ==================== Utility Rituals ====================
        out.accept(NVRituals.PLACER, RitualStats.timed(5000, 10, 5, 0));
        out.accept(NVRituals.GROUNDING, RitualStats.timed(2000, 10, 1, 0));
        out.accept(NVRituals.TORMENT_NEXUS, RitualStats.timed(25000, 0, 20, 1));
        out.accept(NVRituals.ENCHANTED_VITAE, RitualStats.timed(10000, 0, 4, 1));
    }

    protected void add(DeferredHolder<Ritual, ? extends Ritual> ritual, RitualStats stats) {
        ResourceLocation ritualId = ritual.getId();
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

        stats.ambientSound().ifPresent(sound ->
                statsJson.addProperty("ambient_sound", sound.toString()));

        if (stats.perOperation()) {
            statsJson.addProperty("per_operation", true);
        }

        return statsJson;
    }

    @Override
    public String getName() {
        return "NeoVitae Ritual Stats";
    }

    private record RitualEntry(ResourceLocation ritualId, RitualStats stats) {}
}
