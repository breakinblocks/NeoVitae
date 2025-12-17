package com.breakinblocks.neovitae.common.recipe.alchemyarray;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;

import javax.annotation.Nonnull;

public class AlchemyArrayRecipe implements Recipe<AlchemyArrayInput> {
    public static final String RECIPE_TYPE_NAME = "array";

    private final ResourceLocation texture;
    @Nonnull
    private final Ingredient baseInput;
    @Nonnull
    private final Ingredient addedInput;
    @Nonnull
    private final ItemStack output;
    @Nonnull
    private final AlchemyArrayEffectType effectType;

    public AlchemyArrayRecipe(ResourceLocation texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nonnull ItemStack result) {
        this(texture, baseIngredient, addedIngredient, result, AlchemyArrayEffectType.CRAFTING);
    }

    public AlchemyArrayRecipe(ResourceLocation texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nonnull ItemStack result, @Nonnull AlchemyArrayEffectType effectType) {
        this.texture = texture;
        this.baseInput = baseIngredient;
        this.addedInput = addedIngredient;
        this.output = result;
        this.effectType = effectType;
    }

    @Nonnull
    public ResourceLocation getTexture() {
        return texture;
    }

    @Nonnull
    public Ingredient getBaseInput() {
        return baseInput;
    }

    @Nonnull
    public Ingredient getAddedInput() {
        return addedInput;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(getBaseInput());
        list.add(getAddedInput());
        return list;
    }

    @Nonnull
    public ItemStack getOutput() {
        return output;
    }

    @Nonnull
    public AlchemyArrayEffectType getEffectType() {
        return effectType;
    }

    @Override
    public boolean matches(AlchemyArrayInput input, Level level) {
        return baseInput.test(input.base()) && addedInput.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(AlchemyArrayInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.ALCHEMY_ARRAY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMRecipes.ALCHEMY_ARRAY_TYPE.get();
    }
}
