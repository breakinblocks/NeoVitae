package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.function.Consumer;
import java.util.function.Function;

public class ProviderHelper {

    private ExistingFileHelper existingFileHelper;
    public ProviderHelper(ExistingFileHelper helper) {
        this.existingFileHelper = helper;
    }

    public <T> GatherDataEvent.DataProviderFromOutputLookup<TagsProvider<T>> tagsFor(ResourceKey<Registry<T>> key, Consumer<Function<TagKey<T>, TagsProvider.TagAppender<T>>> adder) {
        return (output, lookup) -> new TagsProvider<>(output, key, lookup, NeoVitae.MODID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                adder.accept(this::tag);
            }

        };
    }
}
