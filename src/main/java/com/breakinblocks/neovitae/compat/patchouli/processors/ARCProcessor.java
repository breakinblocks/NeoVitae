package com.breakinblocks.neovitae.compat.patchouli.processors;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patchouli processor for Alchemical Reaction Chamber (ARC) recipes.
 *
 * Example Page:
 * {
 *   "type": "neovitae:crafting_arc",
 *   "heading": "Title",
 *   "recipe": "recipe_id",
 *   "text": "Extra text."
 * }
 */
public class ARCProcessor implements IComponentProcessor {
    private ARCRecipe recipe;
    private HolderLookup.Provider registries;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        this.registries = level.registryAccess();
        ResourceLocation id = ResourceLocation.parse(variables.get("recipe", registries).asString());
        Optional<RecipeHolder<ARCRecipe>> recipeHolder = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(BMRecipes.ARC_TYPE.get())
                .stream()
                .filter(holder -> holder.id().equals(id))
                .findFirst();

        if (recipeHolder.isPresent()) {
            this.recipe = recipeHolder.get().value();
        }

        if (this.recipe == null) {
            LogManager.getLogger().warn("Guidebook missing Alchemical Reaction Chamber recipe {}", id);
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if (recipe == null) {
            return null;
        }

        // Handle indexed output keys (output1, output2, etc.)
        if (key.startsWith("output")) {
            int index = Integer.parseInt(key.substring(6)) - 1;
            List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
            if (allOutputs.size() > index) {
                return IVariable.from(allOutputs.get(index).getFirst(), registries);
            } else {
                return null;
            }
        }

        // Handle chance keys (chance2, chance3, etc. - 2nd output onwards)
        if (key.startsWith("chance")) {
            int index = Integer.parseInt(key.substring(6)) - 2; // Index 0 = 2nd output
            List<Pair<ItemStack, Double>> chanceOutputs = recipe.getChanceOutput();
            if (chanceOutputs.size() > index) {
                double chance = chanceOutputs.get(index).getSecond() * 100;
                if (chance < 1) {
                    return IVariable.wrap("<1", registries);
                }
                return IVariable.wrap(Math.round(chance), registries);
            }
            return null;
        }

        // Handle show_chance keys
        if (key.startsWith("show_chance")) {
            int index = Integer.parseInt(key.substring(11)) - 2; // Index 0 = 2nd output
            List<Pair<ItemStack, Double>> chanceOutputs = recipe.getChanceOutput();
            return IVariable.wrap(chanceOutputs.size() > index, registries);
        }

        return switch (key) {
            case "show_fluid_tooltip" -> IVariable.wrap(
                    recipe.getInputFluid().isPresent() || recipe.getOutputFluid().isPresent(), registries);
            case "input" -> IVariable.wrapList(Arrays.stream(recipe.getInput().getItems())
                    .map(stack -> IVariable.from(stack, registries))
                    .collect(Collectors.toList()), registries);
            case "tool" -> IVariable.wrapList(Arrays.stream(recipe.getTool().getItems())
                    .map(stack -> IVariable.from(stack, registries))
                    .collect(Collectors.toList()), registries);
            case "tooltip_fluid_input" -> {
                if (recipe.getInputFluid().isPresent()) {
                    FluidStack fluid = recipe.getInputFluid().get();
                    String fluidName = Component.translatable(fluid.getFluidType().getDescriptionId()).getString();
                    yield IVariable.wrap(Component.translatable("guide.patchouli.neovitae.arc_processor.fluid",
                            fluid.getAmount(), fluidName).getString(), registries);
                } else {
                    yield IVariable.wrap(Component.translatable("guide.patchouli.neovitae.arc_processor.no_fluid").getString(), registries);
                }
            }
            case "tooltip_fluid_output" -> {
                if (recipe.getOutputFluid().isPresent()) {
                    FluidStack fluid = recipe.getOutputFluid().get();
                    String fluidName = Component.translatable(fluid.getFluidType().getDescriptionId()).getString();
                    yield IVariable.wrap(Component.translatable("guide.patchouli.neovitae.arc_processor.fluid",
                            fluid.getAmount(), fluidName).getString(), registries);
                } else {
                    yield IVariable.wrap(Component.translatable("guide.patchouli.neovitae.arc_processor.no_fluid").getString(), registries);
                }
            }
            default -> null;
        };
    }
}
