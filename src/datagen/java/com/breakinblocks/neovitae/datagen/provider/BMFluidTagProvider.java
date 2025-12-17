package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.fluid.BMFluids;
import com.breakinblocks.neovitae.common.tag.BMTags;

import java.util.concurrent.CompletableFuture;

public class BMFluidTagProvider extends FluidTagsProvider {

    public BMFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, provider, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BMTags.Fluids.LIFE_ESSENCE)
                .add(BMFluids.LIFE_ESSENCE_SOURCE.get())
                .add(BMFluids.LIFE_ESSENCE_FLOWING.get());
    }
}
