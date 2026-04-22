package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.function.Consumer;
import java.util.function.Function;

public class ProviderHelper {

    public ProviderHelper() {
    }

    /**
     * Builds a TagsProvider for a datapack registry type T (DamageType, AltarTier, LivingUpgrade, etc).
     * Exposes a TagAppender&lt;ResourceKey&lt;T&gt;, T&gt; — callers add tag entries via ResourceKey.
     */
    public <T> GatherDataEvent.DataProviderFromOutputLookup<TagsProvider<T>> tagsFor(
            ResourceKey<Registry<T>> key,
            Consumer<Function<TagKey<T>, TagAppender<ResourceKey<T>, T>>> adder) {
        return (output, lookup) -> new TagsProvider<>(output, key, lookup) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                adder.accept(tagKey -> TagAppender.forBuilder(this.getOrCreateRawBuilder(tagKey)));
            }
        };
    }
}
