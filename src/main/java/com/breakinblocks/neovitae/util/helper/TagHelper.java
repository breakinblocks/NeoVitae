package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class TagHelper {

    private TagHelper() {}

    public static Optional<Item> getFirstItem(Registry<Item> registry, String namespace, String path) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
        for (Holder<Item> holder : registry.getTagOrEmpty(tag)) {
            return Optional.of(holder.value());
        }
        return Optional.empty();
    }

    public static boolean tagHasItems(Registry<Item> registry, String namespace, String path) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
        return registry.getTagOrEmpty(tag).iterator().hasNext();
    }

    public static List<String> getOreNames(Registry<Item> registry) {
        return registry.getTags()
                .map(named -> named.key().location())
                .filter(loc -> loc.getNamespace().equals("c") && loc.getPath().startsWith("ores/"))
                .map(loc -> loc.getPath().substring("ores/".length()))
                .filter(name -> !name.isEmpty() && name.indexOf('/') < 0)
                .collect(Collectors.toList());
    }
}
