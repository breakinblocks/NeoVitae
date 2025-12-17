package com.breakinblocks.neovitae.datagen.builder.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.recipe.flask.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recipe builder for all flask recipe types.
 * Flask recipes are processed by the alchemy table and modify alchemy flasks.
 */
public class FlaskRecipeBuilder implements RecipeBuilder {
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected String group;
    protected final List<Ingredient> ingredients = new ArrayList<>();
    protected int syphon = 100;
    protected int ticks = 100;
    protected int minimumTier = 0;

    // For effect recipe
    private Holder<MobEffect> outputEffect;
    private int baseDuration;

    // For fill recipe
    private int maxEffects = 8;

    // For cycle recipe
    private int numCycles = 1;

    // For item transform recipe
    private ItemStack transformOutput;

    // For length recipe
    private Holder<MobEffect> lengthEffect;
    private double lengthMod;

    // For potency recipe
    private Holder<MobEffect> potencyEffect;
    private int amplifier;
    private double ampDurationMod;

    // For effect transform recipe
    private List<Pair<Holder<MobEffect>, Integer>> transformOutputEffects;
    private List<Holder<MobEffect>> transformInputEffects;

    private FlaskRecipeType recipeType;

    private enum FlaskRecipeType {
        EFFECT, FILL, CYCLE, ITEM_TRANSFORM, LENGTH, POTENCY, EFFECT_TRANSFORM
    }

    protected FlaskRecipeBuilder() {
    }

    // ==================== Factory methods ====================

    /**
     * Create a recipe that adds a new effect to a flask.
     */
    public static FlaskRecipeBuilder effect(Holder<MobEffect> effect, int baseDuration) {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.EFFECT;
        builder.outputEffect = effect;
        builder.baseDuration = baseDuration;
        return builder;
    }

    /**
     * Create a recipe that refills a depleted flask.
     */
    public static FlaskRecipeBuilder fill(int maxEffects) {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.FILL;
        builder.maxEffects = maxEffects;
        return builder;
    }

    /**
     * Create a recipe that refills a flask (standard 8 max effects).
     */
    public static FlaskRecipeBuilder fill() {
        return fill(8);
    }

    /**
     * Create a recipe that cycles the effect order in a flask.
     */
    public static FlaskRecipeBuilder cycle(int numCycles) {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.CYCLE;
        builder.numCycles = numCycles;
        return builder;
    }

    /**
     * Create a recipe that transforms a flask into a different flask type.
     */
    public static FlaskRecipeBuilder itemTransform(ItemLike output) {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.ITEM_TRANSFORM;
        builder.transformOutput = new ItemStack(output);
        return builder;
    }

    /**
     * Create a recipe that increases the length modifier of an effect.
     */
    public static FlaskRecipeBuilder length(Holder<MobEffect> effect, double lengthMod) {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.LENGTH;
        builder.lengthEffect = effect;
        builder.lengthMod = lengthMod;
        return builder;
    }

    /**
     * Create a recipe that increases the potency of an effect.
     */
    public static FlaskRecipeBuilder potency(Holder<MobEffect> effect, int amplifier, double ampDurationMod) {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.POTENCY;
        builder.potencyEffect = effect;
        builder.amplifier = amplifier;
        builder.ampDurationMod = ampDurationMod;
        return builder;
    }

    /**
     * Create a recipe that transforms one effect into another.
     */
    public static FlaskRecipeBuilder effectTransform() {
        FlaskRecipeBuilder builder = new FlaskRecipeBuilder();
        builder.recipeType = FlaskRecipeType.EFFECT_TRANSFORM;
        builder.transformOutputEffects = new ArrayList<>();
        builder.transformInputEffects = new ArrayList<>();
        return builder;
    }

    // ==================== Configuration methods ====================

    public FlaskRecipeBuilder addIngredient(ItemLike item) {
        return addIngredient(Ingredient.of(item));
    }

    public FlaskRecipeBuilder addIngredient(TagKey<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public FlaskRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public FlaskRecipeBuilder syphon(int syphon) {
        this.syphon = syphon;
        return this;
    }

    public FlaskRecipeBuilder ticks(int ticks) {
        this.ticks = ticks;
        return this;
    }

    public FlaskRecipeBuilder minimumTier(int tier) {
        this.minimumTier = tier;
        return this;
    }

    /**
     * For effect transform recipe: add an input effect to be consumed.
     */
    public FlaskRecipeBuilder inputEffect(Holder<MobEffect> effect) {
        if (recipeType != FlaskRecipeType.EFFECT_TRANSFORM) {
            throw new IllegalStateException("inputEffect can only be used with effectTransform()");
        }
        this.transformInputEffects.add(effect);
        return this;
    }

    /**
     * For effect transform recipe: add an output effect to be produced.
     */
    public FlaskRecipeBuilder outputEffect(Holder<MobEffect> effect, int duration) {
        if (recipeType != FlaskRecipeType.EFFECT_TRANSFORM) {
            throw new IllegalStateException("outputEffect can only be used with effectTransform()");
        }
        this.transformOutputEffects.add(Pair.of(effect, duration));
        return this;
    }

    @Override
    public FlaskRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public FlaskRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        // Flask recipes modify flasks, so return air
        return null;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("Flask recipe requires at least one ingredient");
        }

        FlaskRecipe recipe = createRecipe();
        // Note: Flask recipes don't have traditional advancements since they modify items
        output.accept(id.withPrefix("flask/"), recipe, null);
    }

    private FlaskRecipe createRecipe() {
        return switch (recipeType) {
            case EFFECT -> new FlaskEffectRecipe(ingredients, outputEffect, baseDuration, syphon, ticks, minimumTier);
            case FILL -> new FlaskFillRecipe(ingredients, maxEffects, syphon, ticks, minimumTier);
            case CYCLE -> new FlaskCycleRecipe(ingredients, numCycles, syphon, ticks, minimumTier);
            case ITEM_TRANSFORM -> new FlaskItemTransformRecipe(ingredients, transformOutput, syphon, ticks, minimumTier);
            case LENGTH -> new FlaskLengthRecipe(ingredients, lengthEffect, lengthMod, syphon, ticks, minimumTier);
            case POTENCY -> new FlaskPotencyRecipe(ingredients, potencyEffect, amplifier, ampDurationMod, syphon, ticks, minimumTier);
            case EFFECT_TRANSFORM -> new FlaskEffectTransformRecipe(ingredients, transformOutputEffects, transformInputEffects, syphon, ticks, minimumTier);
        };
    }
}
