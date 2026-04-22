package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

/**
 * TODO(phase15 stage2): Neo advancement datagen API was removed in 26.1.
 * The committed advancement JSON under src/generated/resources/data/neovitae/advancement/
 * is what the runtime loads. Stubbed for Stage 1.
 *
 * Auroral uses the same "static advancement JSON" approach — the vanilla 26.1 datagen
 * pipeline expects you to hand-author advancement JSON.
 */
public class NVAdvancementProvider implements DataProvider {

    public NVAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "NeoVitae Advancements (stub)";
    }
}
