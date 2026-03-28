package com.breakinblocks.neovitae.util.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TagHelper {

    private TagHelper() {}

    public static Optional<Item> getFirstItem(Registry<Item> registry, String namespace, String path) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
        return registry.getTag(tag).flatMap(set -> set.stream().findFirst().map(Holder::value));
    }

    public static boolean tagHasItems(Registry<Item> registry, String namespace, String path) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, path));
        return registry.getTag(tag).map(set -> set.size() > 0).orElse(false);
    }

    public static List<String> getOreNames(Registry<Item> registry) {
        return registry.getTagNames()
                .filter(tag -> tag.location().getNamespace().equals("c"))
                .filter(tag -> tag.location().getPath().startsWith("ores/"))
                .map(tag -> tag.location().getPath().substring(5))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
