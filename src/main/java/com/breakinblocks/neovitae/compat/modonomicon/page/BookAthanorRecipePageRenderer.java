package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;
import net.minecraft.world.item.ItemStackTemplate;

public class BookAthanorRecipePageRenderer extends BookNVRecipePageRenderer<AthanorRecipe, BookAthanorRecipePage> {

    public BookAthanorRecipePageRenderer(BookAthanorRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphicsExtractor guiGraphics, RecipeDisplayEntry recipeDisplayEntry,
                              int recipeX, int recipeY, int mouseX, int mouseY, boolean second)  {
        if (!second) {
            if (!this.page.getTitle1().isEmpty())
                this.renderTitle(guiGraphics, this.page.getTitle1(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
        } else {
            if (!this.page.getTitle2().isEmpty())
                this.renderTitle(guiGraphics, this.page.getTitle2(), false, BookEntryScreen.PAGE_WIDTH / 2, recipeY - 10);
        }

        recipeY += 8;

        RecipeHolder<AthanorRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        AthanorRecipe recipe = holder.value();

        List<Ingredient> inputs = recipe.getInputs();
        Ingredient primaryInput = inputs.isEmpty() ? Ingredient.of() : inputs.get(0);

        this.drawSlot(guiGraphics, recipeX + 2, recipeY);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 5, recipeY + 3, mouseX, mouseY, primaryInput);

        this.drawSlot(guiGraphics, recipeX + 2, recipeY + 26);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 5, recipeY + 29, mouseX, mouseY, recipe.getTool());

        Component toolLabel = Component.literal("Tool");
        guiGraphics.text(this.font, toolLabel, recipeX + 26, recipeY + 33, 0xFF999999, false);

        this.drawArrow(guiGraphics, recipeX + 50, recipeY + 2);

        List<ItemStackTemplate> guaranteed = recipe.getGuaranteedOutput();
        List<Pair<ItemStackTemplate, Double>> chance = recipe.getChanceOutput();
        int outputX = recipeX + 74;
        int outputIdx = 0;

        for (ItemStackTemplate tpl : guaranteed) {
            int ox = outputX + (outputIdx % 2) * 24;
            int oy = recipeY + (outputIdx / 2) * 24;
            this.drawSlot(guiGraphics, ox, oy);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, tpl.create());
            outputIdx++;
        }

        for (Pair<ItemStackTemplate, Double> pair : chance) {
            if (outputIdx >= 4) break;
            int ox = outputX + (outputIdx % 2) * 24;
            int oy = recipeY + (outputIdx / 2) * 24;
            this.drawSlot(guiGraphics, ox, oy);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, pair.getFirst().create());
            String pct = String.format("%.0f%%", pair.getSecond() * 100);
            guiGraphics.text(this.font, pct, ox + 18, oy + 14, 0xFFAAAAAA, false);
            outputIdx++;
        }
    }
}
