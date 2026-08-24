package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
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

public class FlaskCombinationEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/alchemytable.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private final int syphon;
    private final int ticks;
    private final int minimumTier;

    public FlaskCombinationEmiRecipe(ItemStack inputFlask, List<Ingredient> ingredients, ItemStack outputFlask,
                                     int syphon, int ticks, int minimumTier, ResourceLocation id) {
        super(NVEmiCategories.FLASK_COMBINATION, id, 118, 40);
        this.syphon = syphon;
        this.ticks = ticks;
        this.minimumTier = minimumTier;

        List<EmiIngredient> ins = new ArrayList<>();
        ins.add(EmiStack.of(inputFlask));
        for (Ingredient ingredient : ingredients) {
            ins.add(EmiIngredient.of(ingredient));
        }
        this.inputs = ins;
        this.catalysts = List.of(NVEmiOrbs.atOrAbove(minimumTier, 1));
        this.outputs = List.of(EmiStack.of(outputFlask));
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
                Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(minimumTier)),
                Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(syphon)),
                Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(ticks))
        ), 58, 21, 20, 13);
    }
}
