package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.concurrent.CompletableFuture;

public class NVFluidTagProvider extends FluidTagsProvider {

    public NVFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, provider, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(NVTags.Fluids.ESSENTIA_VITAE)
                .add(NVFluids.ESSENTIA_VITAE_SOURCE.get())
                .add(NVFluids.ESSENTIA_VITAE_FLOWING.get());
        tag(NVTags.Fluids.ESSENTIA_VITAE_SOURCE)
                .add(NVFluids.ESSENTIA_VITAE_SOURCE.get());
        tag(NVTags.Fluids.ESSENTIA_VITAE_FLOWING)
                .add(NVFluids.ESSENTIA_VITAE_FLOWING.get());

        tag(NVTags.Fluids.ANIMATED_SPIRITUS)
                .add(NVFluids.ANIMATED_SPIRITUS_SOURCE.get())
                .add(NVFluids.ANIMATED_SPIRITUS_FLOWING.get());
        tag(NVTags.Fluids.ANIMATED_SPIRITUS_SOURCE)
                .add(NVFluids.ANIMATED_SPIRITUS_SOURCE.get());
        tag(NVTags.Fluids.ANIMATED_SPIRITUS_FLOWING)
                .add(NVFluids.ANIMATED_SPIRITUS_FLOWING.get());
    }
}
