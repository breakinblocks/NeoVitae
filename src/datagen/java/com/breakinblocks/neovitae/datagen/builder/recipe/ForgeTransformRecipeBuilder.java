package com.breakinblocks.neovitae.datagen.builder.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;

import java.util.ArrayList;
import java.util.List;

public class ForgeTransformRecipeBuilder extends BaseRecipeBuilder {

    private double minSpiritus;
    private double drainedWill;
    private final List<Ingredient> catalysts = new ArrayList<>();
    private Ingredient transformInput;

    private ForgeTransformRecipeBuilder(ItemStack result) {
        super(result);
    }

    public static ForgeTransformRecipeBuilder build(ItemLike result) {
        return new ForgeTransformRecipeBuilder(new ItemStack(result));
    }

    public ForgeTransformRecipeBuilder transformInput(ItemLike item) {
        this.transformInput = Ingredient.of(item);
        return this;
    }

    public ForgeTransformRecipeBuilder catalyst(ItemLike item) {
        this.catalysts.add(Ingredient.of(item));
        return this;
    }

    public ForgeTransformRecipeBuilder catalyst(TagKey<Item> tag) {
        this.catalysts.add(Ingredient.of(tag));
        return this;
    }

    public ForgeTransformRecipeBuilder minSpiritus(double minSpiritus) {
        this.minSpiritus = minSpiritus;
        return this;
    }

    public ForgeTransformRecipeBuilder drain(double drain) {
        this.drainedWill = drain;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        Advancement.Builder advBuilder = getBuilder(output, id);
        ForgeTransformRecipe recipe = new ForgeTransformRecipe(minSpiritus, drainedWill, catalysts, transformInput, result);
        output.accept(id.withPrefix("hellfire_forge/"), recipe, advBuilder.build(advancementId(id, "hellfire_forge")));
    }
}
