package com.breakinblocks.neovitae.common.recipe.meteor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.meteor.MeteorLayer;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import java.util.*;

/**
 * Recipe for meteor summoning.
 * Defines the catalyst item, EV cost, explosion radius, and block layers.
 */
public class MeteorRecipe implements Recipe<MeteorInput> {

    public static final String RECIPE_TYPE_NAME = "meteor";

    public static final MapCodec<MeteorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(MeteorRecipe::getInput),
            Codec.INT.fieldOf("syphon").forGetter(MeteorRecipe::getSyphon),
            Codec.FLOAT.fieldOf("explosion").forGetter(MeteorRecipe::getExplosionRadius),
            MeteorLayer.CODEC.listOf().fieldOf("layers").forGetter(MeteorRecipe::getLayerList)
    ).apply(instance, MeteorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MeteorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, MeteorRecipe::getInput,
            ByteBufCodecs.INT, MeteorRecipe::getSyphon,
            ByteBufCodecs.FLOAT, MeteorRecipe::getExplosionRadius,
            MeteorLayer.STREAM_CODEC.apply(ByteBufCodecs.list()), MeteorRecipe::getLayerList,
            MeteorRecipe::new
    );

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

    public void spawnMeteorInWorld(Level level, BlockPos centerPos) {
        if (explosionRadius > 0) {
            level.explode(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(),
                    explosionRadius, Level.ExplosionInteraction.NONE);
        }

        Map<Integer, MeteorLayer> layerMap = new HashMap<>();
        for (MeteorLayer layer : layerList) {
            layerMap.put(layer.getLayerRadius(), layer);
        }

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
        return NVRecipes.METEOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NVRecipes.METEOR_TYPE.get();
    }

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
