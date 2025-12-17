package com.breakinblocks.neovitae.common.recipe.bloodaltar;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

/**
 * Concrete implementation of the Blood Altar recipe.
 * Extends the API abstract class and provides the serializer/type references.
 */
public class BloodAltarRecipe extends com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe {

    public BloodAltarRecipe(Ingredient input, ItemStack result, int minTier, int totalBlood, int craftSpeed, int drainSpeed) {
        super(input, result, minTier, totalBlood, craftSpeed, drainSpeed);
    }

    public BloodAltarRecipe(Ingredient input, ItemStack result, int minTier, int totalBlood, int craftSpeed, int drainSpeed, boolean copyInputComponents) {
        super(input, result, minTier, totalBlood, craftSpeed, drainSpeed, copyInputComponents);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.BLOOD_ALTAR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BMRecipes.BLOOD_ALTAR_TYPE.get();
    }
}
