package com.breakinblocks.neovitae.datagen.builder.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ARCRecipeBuilder extends BaseRecipeBuilder {

    private final TagKey<Item> toolTag;
    private Ingredient input;
    private List<ItemStack> guaranteed = new ArrayList<>();
    private List<Pair<ItemStack, Double>> chanced = new ArrayList<>();
    private FluidStack inputFluid = null;
    private FluidStack outputFluid = null;

    protected ARCRecipeBuilder(TagKey<Item> tag) {
        super(ItemStack.EMPTY);
        if (tag == null) {
            throw new IllegalArgumentException("ARCRecipe tool tag cannot be null");
        }
        this.toolTag = tag;
    }

    public static ARCRecipeBuilder build(TagKey<Item> tag) {
        return new ARCRecipeBuilder(tag);
    }

    public ARCRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public ARCRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public ARCRecipeBuilder input(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public ARCRecipeBuilder guaranteedOutput(ItemStack output) {
        guaranteed.add(output);
        return this;
    }

    public ARCRecipeBuilder chancedOutput(ItemStack stack, double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1, got: " + chance);
        }
        chanced.add(Pair.of(stack, chance));
        return this;
    }

    public ARCRecipeBuilder fluidInput(FluidStack fluidInput) {
        this.inputFluid = fluidInput;
        return this;
    }

    public ARCRecipeBuilder fluidOutput(FluidStack fluidOutput) {
        this.outputFluid = fluidOutput;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (input == null) {
            throw new IllegalStateException("ARCRecipe requires an input ingredient");
        }
        if (guaranteed.isEmpty() && chanced.isEmpty() && outputFluid == null) {
            throw new IllegalStateException("ARCRecipe must have at least one output (guaranteed, chanced, or fluid)");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        ARCRecipe recipe = new ARCRecipe(Ingredient.of(toolTag), input, guaranteed, chanced, Optional.ofNullable(inputFluid), Optional.ofNullable(outputFluid));
        output.accept(makeId(id, toolTag.location()), recipe, advBuilder.build(advancementId(id, "arc")));
    }

    private static ResourceLocation makeId(ResourceLocation id, ResourceLocation tag) {
        String[] segments = tag.getPath().split("/");
        return id.withPrefix("arc/" + segments[segments.length-1] + "/");
    }
}
