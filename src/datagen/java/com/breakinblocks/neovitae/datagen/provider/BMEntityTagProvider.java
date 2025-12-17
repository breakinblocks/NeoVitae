package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.BMTags;

import java.util.concurrent.CompletableFuture;

public class BMEntityTagProvider extends EntityTypeTagsProvider {

    public BMEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, provider, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // TELEPOSE_BLACKLIST - entities that cannot be teleposed
        // Currently empty, but creates the tag file for other mods to add to
        tag(BMTags.Entities.TELEPOSE_BLACKLIST);
    }
}
