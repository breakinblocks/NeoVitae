package com.breakinblocks.neovitae.common.tag;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class MeteorTagInjector {

    private MeteorTagInjector() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdated(TagsUpdatedEvent.ServerDataLoad event) {
        Registry<Item> registry = event.getRegistries().lookupOrThrow(Registries.ITEM);
        Set<Holder<Item>> catalysts = new LinkedHashSet<>();
        registry.get(NVTags.Items.METEOR).ifPresent(named -> named.forEach(catalysts::add));
        for (RecipeHolder<MeteorRecipe> holder : event.getServerResources().getRecipeManager().recipeMap().byType(NVRecipes.METEOR_TYPE.get())) {
            holder.value().getInput().items().forEach(catalysts::add);
        }

        Map<TagKey<Item>, List<Holder<Item>>> tags = new HashMap<>();
        registry.getTags().forEach(named -> tags.put(named.key(), named.stream().toList()));
        if (catalysts.equals(new LinkedHashSet<>(tags.getOrDefault(NVTags.Items.METEOR, List.of())))) {
            return;
        }
        tags.put(NVTags.Items.METEOR, List.copyOf(catalysts));
        registry.prepareTagReload(new TagLoader.LoadResult<>(Registries.ITEM, tags)).apply();
    }
}
