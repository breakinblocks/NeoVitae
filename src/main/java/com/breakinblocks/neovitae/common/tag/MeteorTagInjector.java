package com.breakinblocks.neovitae.common.tag;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class MeteorTagInjector {

    private static volatile ReloadableServerResources pendingResources;

    private MeteorTagInjector() {
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        pendingResources = event.getServerResources();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            return;
        }
        ReloadableServerResources resources = pendingResources;
        if (resources == null) {
            return;
        }
        pendingResources = null;

        Registry<Item> registry = event.getRegistryAccess().registryOrThrow(Registries.ITEM);
        Set<Holder<Item>> catalysts = new LinkedHashSet<>();
        registry.getTag(NVTags.Items.METEOR).ifPresent(named -> named.forEach(catalysts::add));
        for (RecipeHolder<MeteorRecipe> holder : resources.getRecipeManager().getAllRecipesFor(NVRecipes.METEOR_TYPE.get())) {
            for (ItemStack stack : holder.value().getInput().getItems()) {
                catalysts.add(stack.getItemHolder());
            }
        }

        Map<TagKey<Item>, List<Holder<Item>>> tags = new HashMap<>();
        registry.getTags().forEach(pair -> tags.put(pair.getFirst(), pair.getSecond().stream().toList()));
        if (catalysts.equals(new LinkedHashSet<>(tags.getOrDefault(NVTags.Items.METEOR, List.of())))) {
            return;
        }
        tags.put(NVTags.Items.METEOR, List.copyOf(catalysts));
        registry.bindTags(tags);
    }
}
