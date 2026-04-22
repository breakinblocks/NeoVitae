package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.core.HolderGetter;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

// Stores ItemLike+count (not ItemStack) — `new ItemStack(ItemLike)` NPEs during datagen
// ("Components not bound yet") because the Item Holder's component map binds at mod-load.
public abstract class BaseRecipeBuilder implements RecipeBuilder {
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected String group;
    @Nullable
    protected final ItemLike resultItem;
    protected final int resultCount;

    protected BaseRecipeBuilder(ItemStack result) {
        if (result == null || result.isEmpty()) {
            this.resultItem = null;
            this.resultCount = 0;
        } else {
            this.resultItem = result.getItem();
            this.resultCount = result.getCount();
        }
    }

    protected BaseRecipeBuilder(@Nullable ItemLike result, int count) {
        this.resultItem = result;
        this.resultCount = count;
    }

    protected BaseRecipeBuilder(@Nullable ItemLike result) {
        this(result, 1);
    }

    private static HolderGetter<Item> itemGetter;
    private static HolderGetter<Fluid> fluidGetter;

    public static void bindItemGetter(HolderGetter<Item> getter) {
        itemGetter = getter;
    }

    public static void bindFluidGetter(HolderGetter<Fluid> getter) {
        fluidGetter = getter;
    }

    public static Ingredient ingredientOf(TagKey<Item> tag) {
        if (itemGetter == null) {
            throw new IllegalStateException("BaseRecipeBuilder.bindItemGetter() must be called before building tag-based ingredients");
        }
        return Ingredient.of(itemGetter.getOrThrow(tag));
    }

    public static SizedFluidIngredient sizedFluidOf(TagKey<Fluid> tag, int amount) {
        if (fluidGetter == null) {
            throw new IllegalStateException("BaseRecipeBuilder.bindFluidGetter() must be called before building tag-based fluid ingredients");
        }
        return new SizedFluidIngredient(FluidIngredient.of(fluidGetter.getOrThrow(tag)), amount);
    }

    protected ItemStackTemplate resultTemplate() {
        if (resultItem == null) {
            return new ItemStackTemplate(Items.STONE, 1);
        }
        return new ItemStackTemplate(resultItem.asItem(), Math.max(resultCount, 1));
    }

    @Deprecated
    protected ItemStack resultStack() {
        if (resultItem == null || resultCount <= 0) return ItemStack.EMPTY;
        return new ItemStackTemplate(resultItem.asItem(), resultCount).create();
    }

    @Override
    public BaseRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public BaseRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        Identifier id = resultItem == null
                ? Identifier.withDefaultNamespace("unknown")
                : BuiltInRegistries.ITEM.getKey(resultItem.asItem());
        return recipeKey(id);
    }

    protected static ResourceKey<Recipe<?>> recipeKey(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    public Advancement.Builder getBuilder(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        Advancement.Builder advBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advBuilder::addCriterion);
        return advBuilder;
    }

    public Identifier advancementId(ResourceKey<Recipe<?>> id, String folder) {
        return id.identifier().withPrefix("recipes/" + folder + "/");
    }
}
