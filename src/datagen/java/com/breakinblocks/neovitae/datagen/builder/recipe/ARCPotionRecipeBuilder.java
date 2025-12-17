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
import com.breakinblocks.neovitae.common.recipe.arc.ARCPotionRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Recipe builder for ARC Potion recipes - recipes that transfer potion effects
 * from the tool (lingering alchemy flask) to the output item.
 */
public class ARCPotionRecipeBuilder extends BaseRecipeBuilder {

    private final TagKey<Item> toolTag;
    private Ingredient input;
    private List<ItemStack> guaranteed = new ArrayList<>();
    private List<Pair<ItemStack, Double>> chanced = new ArrayList<>();
    private FluidStack inputFluid = null;
    private FluidStack outputFluid = null;

    protected ARCPotionRecipeBuilder(TagKey<Item> tag) {
        super(ItemStack.EMPTY);
        if (tag == null) {
            throw new IllegalArgumentException("ARCPotionRecipe tool tag cannot be null");
        }
        this.toolTag = tag;
    }

    public static ARCPotionRecipeBuilder build(TagKey<Item> tag) {
        return new ARCPotionRecipeBuilder(tag);
    }

    public ARCPotionRecipeBuilder input(ItemLike item) {
        return input(Ingredient.of(item));
    }

    public ARCPotionRecipeBuilder input(ItemStack stack) {
        return input(Ingredient.of(stack));
    }

    public ARCPotionRecipeBuilder input(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public ARCPotionRecipeBuilder guaranteedOutput(ItemStack output) {
        guaranteed.add(output);
        return this;
    }

    public ARCPotionRecipeBuilder chancedOutput(ItemStack stack, double chance) {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance must be between 0 and 1, got: " + chance);
        }
        chanced.add(Pair.of(stack, chance));
        return this;
    }

    public ARCPotionRecipeBuilder fluidInput(FluidStack fluidInput) {
        this.inputFluid = fluidInput;
        return this;
    }

    public ARCPotionRecipeBuilder fluidOutput(FluidStack fluidOutput) {
        this.outputFluid = fluidOutput;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        if (input == null) {
            throw new IllegalStateException("ARCPotionRecipe requires an input ingredient");
        }
        if (guaranteed.isEmpty() && chanced.isEmpty() && outputFluid == null) {
            throw new IllegalStateException("ARCPotionRecipe must have at least one output (guaranteed, chanced, or fluid)");
        }
        Advancement.Builder advBuilder = getBuilder(output, id);
        ARCPotionRecipe recipe = new ARCPotionRecipe(Ingredient.of(toolTag), input, guaranteed, chanced, Optional.ofNullable(inputFluid), Optional.ofNullable(outputFluid));
        output.accept(makeId(id, toolTag.location()), recipe, advBuilder.build(advancementId(id, "arc_potion")));
    }

    private static ResourceLocation makeId(ResourceLocation id, ResourceLocation tag) {
        String[] segments = tag.getPath().split("/");
        return id.withPrefix("arc_potion/" + segments[segments.length-1] + "/");
    }
}
