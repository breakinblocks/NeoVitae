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
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableRecipe;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patchouli processor for Alchemy Table recipes.
 *
 * Example Page:
 * {
 *   "type": "neovitae:crafting_alchemy_table",
 *   "heading": "Title",
 *   "recipe": "recipe_id",
 *   "text": "Extra text."
 * }
 */
public class AlchemyTableProcessor implements IComponentProcessor {
    private AlchemyTableRecipe recipe;
    private HolderLookup.Provider registries;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        Optional<RecipeHolder<AlchemyTableRecipe>> recipeHolder = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(BMRecipes.ALCHEMY_TABLE_TYPE.get())
                .stream()
                .filter(holder -> holder.id().equals(id))
                .findFirst();

        if (recipeHolder.isPresent()) {
            this.recipe = recipeHolder.get().value();
        }

        if (this.recipe == null) {
            LogManager.getLogger().warn("Guidebook missing Alchemy Table recipe {}", id);
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) {
            return null;
        }
        if (key.startsWith("input")) {
            int index = Integer.parseInt(key.substring(5)) - 1;
            if (recipe.getInput().size() > index) {
                return IVariable.wrapList(Arrays.stream(recipe.getInput().get(index).getItems())
                        .map(stack -> IVariable.from(stack, registries))
                        .collect(Collectors.toList()), registries);
            } else {
                return null;
            }
        }
        return switch (key) {
            case "output" -> IVariable.from(recipe.getOutput(), registries);
            case "syphon" -> IVariable.wrap(recipe.getSyphon(), registries);
            case "time" -> IVariable.wrap(recipe.getTicks(), registries);
            case "tier" -> IVariable.wrap(recipe.getMinimumTier(), registries);
            case "orb" -> getOrbForTier(recipe.getMinimumTier());
            default -> null;
        };
    }

    private IVariable getOrbForTier(int tier) {
        return switch (tier) {
            case 0, 1 -> IVariable.from(new ItemStack(BMItems.ORB_WEAK.get()), registries);
            case 2 -> IVariable.from(new ItemStack(BMItems.ORB_APPRENTICE.get()), registries);
            case 3 -> IVariable.from(new ItemStack(BMItems.ORB_MAGICIAN.get()), registries);
            case 4 -> IVariable.from(new ItemStack(BMItems.ORB_MASTER.get()), registries);
            case 5 -> IVariable.from(new ItemStack(BMItems.ORB_ARCHMAGE.get()), registries);
            case 6 -> IVariable.from(new ItemStack(BMItems.ORB_TRANSCENDENT.get()), registries);
            default -> {
                LogManager.getLogger().warn("Guidebook unable to find large enough Blood Orb for tier {}", tier);
                yield IVariable.from(new ItemStack(Items.BARRIER), registries);
            }
        };
    }
}
