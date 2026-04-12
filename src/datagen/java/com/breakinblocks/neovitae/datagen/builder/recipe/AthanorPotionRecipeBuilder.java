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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorPotionRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Recipe builder for Athanor Potion recipes - recipes that transfer potion effects
 * from the tool (lingering alchemy flask) to the output item.
 */
public class AthanorPotionRecipeBuilder extends BaseRecipeBuilder {

    private final TagKey<Item> toolTag;
    private Ingredient input;
    private List<ItemStack> guaranteed = new ArrayList<>();
    private List<Pair<ItemStack, Double>> chanced = new ArrayList<>();
    private SizedFluidIngredient inputFluid = null;
    private FluidStack outputFluid = null;

    protected AthanorPotionRecipeBuilder(TagKey<Item> tag) {
        super(ItemStack.EMPTY);
        if (tag == null) {
            throw new IllegalArgumentException("AthanorPotionRecipe tool tag cannot be null");
        }
        this.toolTag = tag;
    }

    public static AthanorPotionRecipeBuilder build(TagKey<Item> tag) {
        return new AthanorPotionRecipeBuilder(tag);
    }

    public AthanorPotionRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public AthanorPotionRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public AthanorPotionRecipeBuilder input(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public AthanorPotionRecipeBuilder guaranteedOutput(ItemStack output) {
        guaranteed.add(output);
        return this;
    }

    public AthanorPotionRecipeBuilder chancedOutput(ItemStack stack, double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1, got: " + chance);
        }
        chanced.add(Pair.of(stack, chance));
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(SizedFluidIngredient fluidInput) {
        this.inputFluid = fluidInput;
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(FluidStack fluidInput) {
        this.inputFluid = SizedFluidIngredient.of(fluidInput);
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(Fluid fluid, int amount) {
        this.inputFluid = SizedFluidIngredient.of(fluid, amount);
        return this;
    }

    public AthanorPotionRecipeBuilder fluidInput(TagKey<Fluid> tag, int amount) {
        this.inputFluid = SizedFluidIngredient.of(tag, amount);
        return this;
    }

    public AthanorPotionRecipeBuilder fluidOutput(FluidStack fluidOutput) {
        this.outputFluid = fluidOutput;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (input == null) {
            throw new IllegalStateException("AthanorPotionRecipe requires an input ingredient");
        }
        if (guaranteed.isEmpty() && chanced.isEmpty() && outputFluid == null) {
            throw new IllegalStateException("AthanorPotionRecipe must have at least one output (guaranteed, chanced, or fluid)");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        AthanorPotionRecipe recipe = new AthanorPotionRecipe(Ingredient.of(toolTag), input, guaranteed, chanced, Optional.ofNullable(inputFluid), Optional.ofNullable(outputFluid));
        output.accept(makeId(id, toolTag.location()), recipe, advBuilder.build(advancementId(id, "athanor_potion")));
    }

    private static ResourceLocation makeId(ResourceLocation id, ResourceLocation tag) {
        String[] segments = tag.getPath().split("/");
        return id.withPrefix("athanor_potion/" + segments[segments.length-1] + "/");
    }
}
