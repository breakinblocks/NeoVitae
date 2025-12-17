package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.concurrent.CompletableFuture;

public class BMSpriteSourceProvider extends SpriteSourceProvider {

    public BMSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void gather() {
        // Add models directory to the blocks atlas for custom model textures
        atlas(BLOCKS_ATLAS).addSource(new DirectoryLister("models", "models/"));
    }
}
