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

/**
 * 26.1 note: ItemStack construction ({@code new ItemStack(ItemLike)}) will NPE during datagen
 * with "Components not bound yet" because the item Holder's component map is populated after
 * registry bake. This base class therefore stores {@code ItemLike + count} and materializes the
 * ItemStack lazily via {@link #resultStack()}. Subclasses that need to compare the result against
 * ItemStack.EMPTY should instead compare against the ItemLike field.
 */
public abstract class BaseRecipeBuilder implements RecipeBuilder {
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected String group;
    @Nullable
    protected final ItemLike resultItem;
    protected final int resultCount;

    protected BaseRecipeBuilder(ItemStack result) {
        // Accept ItemStack for source compatibility but immediately decompose — ItemStack
        // construction already happened (or is happening) in the caller; the safe path is to
        // extract the ItemLike and count without touching .components().
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

    /**
     * Construct the result ItemStack lazily via {@link net.minecraft.world.item.ItemStackTemplate},
     * which is the 26.1 datagen-safe path. The direct ItemStack ctors NPE during datagen with
     * "Components not bound yet" because the Item Holder's component map is populated at
     * mod-load, not during datagen. ItemStackTemplate doesn't trigger the component lookup.
     */
    protected ItemStack resultStack() {
        if (resultItem == null || resultCount <= 0) return ItemStack.EMPTY;
        return new net.minecraft.world.item.ItemStackTemplate(resultItem.asItem(), resultCount).create();
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

    /**
     * 26.1 recipe contract: each builder reports its default ResourceKey.
     * Subclasses override when the default derives from more than the result item id.
     */
    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        Identifier id = resultItem == null
                ? Identifier.withDefaultNamespace("unknown")
                : net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(resultItem.asItem());
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
