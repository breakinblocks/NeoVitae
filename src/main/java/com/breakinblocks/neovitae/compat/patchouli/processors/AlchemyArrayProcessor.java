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
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patchouli processor for Alchemy Array recipes.
 *
 * Example Page:
 * {
 *   "type": "neovitae:crafting_array",
 *   "heading": "Title",
 *   "recipe": "recipe_id",
 *   "text": "Extra text."
 * }
 */
public class AlchemyArrayProcessor implements IComponentProcessor {
    private AlchemyArrayRecipe recipe;
    private HolderLookup.Provider registries;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        Optional<RecipeHolder<AlchemyArrayRecipe>> recipeHolder = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(BMRecipes.ALCHEMY_ARRAY_TYPE.get())
                .stream()
                .filter(holder -> holder.id().equals(id))
                .findFirst();

        if (recipeHolder.isPresent()) {
            this.recipe = recipeHolder.get().value();
        }

        if (this.recipe == null) {
            LogManager.getLogger().warn("Guidebook missing Alchemy Array recipe {}", id);
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) {
            return null;
        }
        return switch (key) {
            case "baseinput" -> IVariable.wrapList(Arrays.stream(recipe.getBaseInput().getItems())
                    .map(stack -> IVariable.from(stack, registries))
                    .collect(Collectors.toList()), registries);
            case "addedinput" -> IVariable.wrapList(Arrays.stream(recipe.getAddedInput().getItems())
                    .map(stack -> IVariable.from(stack, registries))
                    .collect(Collectors.toList()), registries);
            case "output" -> IVariable.from(recipe.getOutput(), registries);
            default -> null;
        };
    }
}
