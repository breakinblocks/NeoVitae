package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import com.breakinblocks.neovitae.compat.viewer.MeteorOutputEstimator;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MeteorEmiRecipe extends BasicEmiRecipe {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    private static final int WIDTH = 170;
    private static final int HEIGHT = 120;
    private static final int GRAY = 0x808080;
    private static final int DARK_GRAY = 0x404040;
    private static final int SLOTS_PER_ROW = 8;
    private static final int START_X = 5;
    private static final int START_Y = 70;

    private final MeteorRecipe recipe;
    private final List<MeteorOutputEstimator.Estimate> estimates;

    public MeteorEmiRecipe(MeteorRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.METEOR, id, WIDTH, HEIGHT);
        this.recipe = recipe;
        this.estimates = MeteorOutputEstimator.estimate(recipe);

        this.catalysts = List.of(EmiIngredient.of(recipe.getInput()));

        List<EmiStack> outs = new ArrayList<>();
        for (MeteorOutputEstimator.Estimate estimate : estimates) {
            for (ItemStack stack : estimate.stacks()) {
                outs.add(EmiStack.of(stack));
            }
        }
        this.outputs = outs;
        this.inputs = List.of();
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addText(Component.literal("Cost: " + DECIMAL_FORMAT.format(recipe.getSyphon()) + " EV"), 30, 3, GRAY, false);
        widgets.addText(Component.literal("Explosion: " + recipe.getExplosionRadius()), 30, 13, GRAY, false);
        int diameter = MeteorOutputEstimator.maxRadius(recipe) * 2 + 1;
        widgets.addText(Component.literal("Size: " + diameter + " Blocks"), 30, 23, GRAY, false);
        widgets.addText(Component.literal("Catalyst:"), 0, 40, DARK_GRAY, false);
        widgets.addText(Component.literal("Outputs:"), 0, 58, DARK_GRAY, false);

        widgets.addSlot(catalysts.get(0), 49, 37).catalyst(true);

        for (int i = 0; i < estimates.size(); i++) {
            MeteorOutputEstimator.Estimate estimate = estimates.get(i);
            List<EmiIngredient> stacks = new ArrayList<>();
            for (ItemStack stack : estimate.stacks()) {
                stacks.add(EmiStack.of(stack));
            }
            int x = START_X + (i % SLOTS_PER_ROW) * 18;
            int y = START_Y + (i / SLOTS_PER_ROW) * 18;

            var slot = widgets.addSlot(EmiIngredient.of(stacks), x, y).recipeContext(this);
            slot.appendTooltip(Component.translatable("jei.neovitae.recipe.meteor.estimate",
                    DECIMAL_FORMAT.format(estimate.count()), String.format("%.1f", estimate.percentage())));
            if (estimate.poolSize() > 1) {
                slot.appendTooltip(Component.translatable("jei.neovitae.recipe.meteor.random_pool",
                        DECIMAL_FORMAT.format(estimate.poolSize())));
            }
        }
    }
}
