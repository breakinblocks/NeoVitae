package com.breakinblocks.neovitae.compat.patchouli.processors;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patchouli processor for Hellfire Forge (Soul Forge / Tartaric Forge) recipes.
 *
 * Example Page:
 * {
 *   "type": "neovitae:crafting_soulforge",
 *   "heading": "Title",
 *   "recipe": "recipe_id",
 *   "text": "Extra text."
 * }
 */
public class TartaricForgeProcessor implements IComponentProcessor {
    private ForgeRecipe recipe;
    private HolderLookup.Provider registries;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        Optional<RecipeHolder<ForgeRecipe>> recipeHolder = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(BMRecipes.SOUL_FORGE_TYPE.get())
                .stream()
                .filter(holder -> holder.id().equals(id))
                .findFirst();

        if (recipeHolder.isPresent()) {
            this.recipe = recipeHolder.get().value();
        }

        if (this.recipe == null) {
            LogManager.getLogger().warn("Guidebook missing Hellfire Forge recipe {}", id);
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) {
            return null;
        }
        if (key.startsWith("input")) {
            int index = Integer.parseInt(key.substring(5)) - 1;
            if (recipe.getCraftingIngredients().size() > index) {
                return IVariable.wrapList(Arrays.stream(recipe.getCraftingIngredients().get(index).getItems())
                        .map(stack -> IVariable.from(stack, registries))
                        .collect(Collectors.toList()), registries);
            } else {
                return null;
            }
        }
        return switch (key) {
            case "output" -> IVariable.from(recipe.getOutput(), registries);
            case "willrequired" -> IVariable.wrap(recipe.getMinWill(), registries);
            case "willdrain" -> IVariable.wrap(recipe.getDrain(), registries);
            case "will" -> getGemForWillAmount(recipe.getMinWill());
            default -> null;
        };
    }

    private IVariable getGemForWillAmount(double minWill) {
        if (minWill <= 1) {
            return IVariable.from(new ItemStack(BMItems.MONSTER_SOUL_RAW.get()), registries);
        } else if (minWill <= 64) {
            return IVariable.from(new ItemStack(BMItems.SOUL_GEM_PETTY.get()), registries);
        } else if (minWill <= 256) {
            return IVariable.from(new ItemStack(BMItems.SOUL_GEM_LESSER.get()), registries);
        } else if (minWill <= 1024) {
            return IVariable.from(new ItemStack(BMItems.SOUL_GEM_COMMON.get()), registries);
        } else if (minWill <= 4096) {
            return IVariable.from(new ItemStack(BMItems.SOUL_GEM_GREATER.get()), registries);
        } else {
            LogManager.getLogger().warn("Guidebook could not find a large enough Tartaric Gem for will amount {}", minWill);
            return IVariable.from(new ItemStack(Items.BARRIER), registries);
        }
    }
}
