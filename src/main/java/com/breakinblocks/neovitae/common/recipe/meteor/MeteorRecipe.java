package com.breakinblocks.neovitae.common.recipe.meteor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import java.util.*;

/**
 * Recipe for meteor summoning.
 * Defines the catalyst item, LP cost, explosion radius, and block layers.
 */
public class MeteorRecipe implements Recipe<MeteorInput> {

    public static final String RECIPE_TYPE_NAME = "meteor";

    private final Ingredient input;
    private final int syphon;
    private final float explosionRadius;
    private final List<MeteorLayer> layerList;

    public MeteorRecipe(Ingredient input, int syphon, float explosionRadius, List<MeteorLayer> layerList) {
        this.input = input;
        this.syphon = syphon;
        this.explosionRadius = explosionRadius;
        this.layerList = new ArrayList<>(layerList);
    }

    /**
     * Spawns the meteor in the world at the given position.
     * Creates an explosion if configured, then builds layers from inside out.
     */
    public void spawnMeteorInWorld(Level level, BlockPos centerPos) {
        if (explosionRadius > 0) {
            level.explode(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(),
                    explosionRadius, Level.ExplosionInteraction.TNT);
        }

        // Build a map of layers by radius
        Map<Integer, MeteorLayer> layerMap = new HashMap<>();
        for (MeteorLayer layer : layerList) {
            layerMap.put(layer.getLayerRadius(), layer);
        }

        // Sort radii and build from smallest to largest
        List<Integer> keyList = new ArrayList<>(layerMap.keySet());
        Collections.sort(keyList);

        int prevRadius = -1;
        for (Integer radius : keyList) {
            MeteorLayer layer = layerMap.get(radius);
            layer.buildLayer(level, centerPos, prevRadius);
            prevRadius = layer.getLayerRadius();
        }
    }

    @Override
    public boolean matches(MeteorInput input, Level level) {
        return this.input.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(MeteorInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.METEOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMRecipes.METEOR_TYPE.get();
    }

    // Getters

    public Ingredient getInput() {
        return input;
    }

    public int getSyphon() {
        return syphon;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    public List<MeteorLayer> getLayerList() {
        return layerList;
    }
}
