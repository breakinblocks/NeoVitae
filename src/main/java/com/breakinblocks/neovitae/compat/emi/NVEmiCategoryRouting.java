package com.breakinblocks.neovitae.compat.emi;

import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeSpiritusInfusionRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;
import com.breakinblocks.neovitae.common.tag.NVTags;
import dev.emi.emi.api.recipe.EmiRecipeCategory;

public final class NVEmiCategoryRouting {

    private NVEmiCategoryRouting() {
    }

    public static EmiRecipeCategory forgeCategory(ForgeRecipe recipe) {
        boolean upgrade = recipe instanceof ForgeUpgradeRecipe
                || recipe instanceof ForgeTransformRecipe
                || recipe instanceof ForgeSpiritusInfusionRecipe
                || recipe.getOutput().is(NVTags.Items.SPIRITUS_GEM);
        return upgrade ? NVEmiCategories.FORGE_UPGRADE : NVEmiCategories.HELLFIRE_FORGE;
    }
}
