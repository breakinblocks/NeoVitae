package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AlchemyArrayCraftingEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/binding.png");

    public AlchemyArrayCraftingEmiRecipe(AlchemyArrayRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.ALCHEMY_ARRAY_CRAFTING, id, 100, 30);
        this.inputs = List.of(EmiIngredient.of(recipe.getBaseInput()), EmiIngredient.of(recipe.getAddedInput()));
        this.outputs = recipe.getOutput().isEmpty() ? List.of() : List.of(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 100, 30, 0, 0);
        widgets.addSlot(inputs.get(0), 0, 5).drawBack(false);
        widgets.addSlot(inputs.get(1), 29, 3).drawBack(false);
        if (!outputs.isEmpty()) {
            widgets.addSlot(outputs.get(0), 73, 5).drawBack(false).recipeContext(this);
        }
    }
}
