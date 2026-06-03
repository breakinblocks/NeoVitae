package com.breakinblocks.neovitae.client.event;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import javax.annotation.Nullable;

/**
 * Caches recipes pushed from the server via
 * {@link OnDatapackSyncEvent#sendRecipes}. Client-side code
 * (JEI plugin, Modonomicon pages, particle effects) that needs to enumerate or look up
 * recipes reads from {@link #get()} or {@link #byKey(ResourceKey)}.
 */
@EventBusSubscriber(modid = NeoVitae.MODID, value = Dist.CLIENT)
public final class ClientRecipeCache {
    private static RecipeMap recipes = RecipeMap.EMPTY;

    private ClientRecipeCache() {}

    public static RecipeMap get() {
        return recipes;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> RecipeHolder<T> byKey(ResourceKey<Recipe<?>> key) {
        RecipeHolder<?> holder = recipes.byKey(key);
        return (RecipeHolder<T>) holder;
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        recipes = event.getRecipeMap();
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        recipes = RecipeMap.EMPTY;
    }
}
