package com.breakinblocks.neovitae.datagen.builder.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.tiered.FluidTieredRecipe;

import java.util.List;
import java.util.Map;

/**
 * Builder for FluidTieredRecipe - recipes that combine fluid containers to increase tier.
 */
public class TieredRecipeBuilder extends BaseRecipeBuilder {
    protected final List<String> rows = Lists.newArrayList();
    protected final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    protected final RecipeCategory category;

    protected int primary;
    protected int secondary;

    public TieredRecipeBuilder(RecipeCategory category, ItemStack result) {
        super(result);
        this.category = category;
    }

    public static TieredRecipeBuilder fluid(RecipeCategory category, ItemLike result) {
        return new TieredRecipeBuilder(category, new ItemStack(result, 1));
    }

    public TieredRecipeBuilder primary(int primary) {
        this.primary = primary;
        return this;
    }

    public TieredRecipeBuilder secondary(int secondary) {
        this.secondary = secondary;
        return this;
    }

    public TieredRecipeBuilder define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    public TieredRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(tag));
    }

    public TieredRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredient);
            return this;
        }
    }

    public TieredRecipeBuilder pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(key, rows);
        Advancement.Builder advBuilder = getBuilder(output, id);
        FluidTieredRecipe recipe = new FluidTieredRecipe(
                CraftingBookCategory.valueOf(this.category.name()),
                shapedrecipepattern, primary, secondary, result);
        output.accept(id, recipe, advBuilder.build(advancementId(id, this.category.getFolderName())));
    }
}
