package com.breakinblocks.neovitae.compat.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class NVJeiRecipeIds {

    private NVJeiRecipeIds() {
    }

    private static final Map<Object, ResourceLocation> IDS = new IdentityHashMap<>();

    public static void clear() {
        IDS.clear();
    }

    public static <T extends Recipe<?>> List<T> track(List<RecipeHolder<T>> holders) {
        List<T> values = new ArrayList<>(holders.size());
        for (RecipeHolder<T> holder : holders) {
            IDS.put(holder.value(), holder.id());
            values.add(holder.value());
        }
        return values;
    }

    public static void put(Object recipe, ResourceLocation id) {
        IDS.put(recipe, id);
    }

    @Nullable
    public static ResourceLocation get(Object recipe) {
        return IDS.get(recipe);
    }
}
