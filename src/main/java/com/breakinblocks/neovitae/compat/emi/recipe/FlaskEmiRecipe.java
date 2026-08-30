package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskItemTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FlaskEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/alchemytable.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private final FlaskRecipe recipe;

    public FlaskEmiRecipe(FlaskRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.FLASK, id, 118, 40);
        this.recipe = recipe;

        List<EmiIngredient> ins = new ArrayList<>();
        for (Ingredient ingredient : recipe.getInput()) {
            ins.add(EmiIngredient.of(ingredient));
        }
        ItemStack exampleOutput = recipe.getOutput(recipe.getExampleFlask(), recipe.getExampleEffects());
        if (recipe instanceof FlaskItemTransformRecipe transform) {
            if (ins.size() < 6) {
                ins.add(EmiIngredient.of(List.of(EmiStack.of(recipe.getExampleFlask()), EmiStack.of(new ItemStack(NVItems.ALCHEMY_FLASK.get())))));
            }
            this.outputs = List.of(EmiStack.of(exampleOutput), EmiStack.of(transform.getOutputItem().copy()));
        } else {
            if (ins.size() < 6) {
                ins.add(EmiStack.of(recipe.getExampleFlask()));
            }
            this.outputs = List.of(EmiStack.of(exampleOutput));
        }
        this.inputs = ins;
        this.catalysts = List.of(NVEmiOrbs.atOrAbove(recipe.getMinimumTier(), 1));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 118, 40, 0, 0);

        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), (i % 3) * 18, (i / 3) * 18).drawBack(false);
        }
        widgets.addSlot(catalysts.get(0), 60, 0).drawBack(false).catalyst(true);
        widgets.addSlot(outputs.get(0), 91, 13).drawBack(false).recipeContext(this);

        widgets.addTooltipText(List.of(
                Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(recipe.getMinimumTier())),
                Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(recipe.getSyphon())),
                Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(recipe.getTicks()))
        ), 58, 21, 20, 13);
    }
}
