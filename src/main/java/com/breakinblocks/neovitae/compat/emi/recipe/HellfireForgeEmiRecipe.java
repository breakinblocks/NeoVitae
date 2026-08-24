package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.Ingredient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HellfireForgeEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/hellfire_forge.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final ItemLike[] GEM_ITEMS = {
            NVItems.RAW_SPIRITUS.get(), NVItems.SPIRITUS_GEM_PETTY.get(), NVItems.SPIRITUS_GEM_LESSER.get(),
            NVItems.SPIRITUS_GEM_COMMON.get(), NVItems.SPIRITUS_GEM_GREATER.get(), NVItems.SPIRITUS_GEM_GRAND.get()
    };
    private static final double[] GEM_CAPACITY = {16, 64, 256, 1024, 4096, 16384};

    private final ForgeRecipe recipe;

    public HellfireForgeEmiRecipe(ForgeRecipe recipe, ResourceLocation id, EmiRecipeCategory category) {
        super(category, id, 100, 40);
        this.recipe = recipe;

        List<EmiIngredient> ins = new ArrayList<>();
        for (Ingredient ingredient : recipe.getCraftingIngredients()) {
            ins.add(EmiIngredient.of(ingredient));
        }
        this.inputs = ins;

        List<EmiIngredient> gems = new ArrayList<>();
        for (int i = 0; i < GEM_ITEMS.length; i++) {
            if (GEM_CAPACITY[i] >= recipe.getMinSpiritus()) {
                gems.add(EmiStack.of(GEM_ITEMS[i]));
            }
        }
        this.catalysts = gems.isEmpty() ? List.of() : List.of(EmiIngredient.of(gems));
        this.outputs = List.of(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 100, 40, 0, 0);

        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), (i % 2) * 18, (i / 2) * 18).drawBack(false);
        }
        if (!catalysts.isEmpty()) {
            widgets.addSlot(catalysts.get(0), 42, 0).drawBack(false).catalyst(true);
        }
        widgets.addSlot(outputs.get(0), 73, 13).drawBack(false).recipeContext(this);

        widgets.addTooltipText(List.of(
                Component.translatable("jei.neovitae.recipe.minimum_spiritus", DECIMAL_FORMAT.format(recipe.getMinSpiritus())),
                Component.translatable("jei.neovitae.recipe.spiritus_drained", DECIMAL_FORMAT.format(recipe.getDrain()))
        ), 40, 21, 20, 13);
    }
}
