package com.breakinblocks.neovitae.compat.patchouli.processors;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patchouli processor for Blood Altar recipes.
 *
 * Example Page:
 * {
 *   "type": "neovitae:crafting_altar",
 *   "heading": "Title",
 *   "recipe": "recipe_id",
 *   "text": "Extra text."
 * }
 */
public class BloodAltarProcessor implements IComponentProcessor {
    private BloodAltarRecipe recipe;
    private HolderLookup.Provider registries;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        Optional<RecipeHolder<BloodAltarRecipe>> recipeHolder = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(BMRecipes.BLOOD_ALTAR_TYPE.get())
                .stream()
                .filter(holder -> holder.id().equals(id))
                .findFirst();

        if (recipeHolder.isPresent()) {
            this.recipe = recipeHolder.get().value();
        }

        if (this.recipe == null) {
            LogManager.getLogger().warn("Guidebook missing Blood Altar recipe {}", id);
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) {
            return null;
        }
        return switch (key) {
            case "input" -> IVariable.wrapList(Arrays.stream(recipe.getInput().getItems())
                    .map(stack -> IVariable.from(stack, registries))
                    .collect(Collectors.toList()), registries);
            case "output" -> IVariable.from(recipe.getResult(), registries);
            case "tier" -> IVariable.wrap(recipe.getMinTier() + 1, registries);
            case "lp" -> IVariable.wrap(recipe.getTotalBlood(), registries);
            default -> null;
        };
    }
}
