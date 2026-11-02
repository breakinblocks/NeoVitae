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
        int outputCount = recipe.getGuaranteedOutput().size() + recipe.getChanceOutput().size();
        int outputWidth = outputCount > 1 ? 44 : 22;
        int startX = (BookEntryScreen.PAGE_WIDTH - (44 + 26 + outputWidth)) / 2;
        int toolX = startX + 46;
        for (int idx = 0; idx < inputs.size() && idx < 6; idx++) {
            int ix = startX + (idx % 2) * 22;
            int iy = recipeY + (idx / 2) * 22;
            this.drawSlot(guiGraphics, ix, iy);
            this.parentScreen.renderIngredient(guiGraphics, ix + 3, iy + 3, mouseX, mouseY, inputs.get(idx));
        }
        this.drawSlot(guiGraphics, toolX, recipeY + 38);
        if (recipe.getTool().isPresent()) {
            this.parentScreen.renderIngredient(guiGraphics, toolX + 3, recipeY + 41, mouseX, mouseY, recipe.getTool().get());
        }

        Component toolLabel = Component.literal("Tool");
        guiGraphics.text(this.font, toolLabel, toolX, recipeY + 62, 0xFF999999, false);

        this.drawArrow(guiGraphics, toolX + 2, recipeY + 10);

        List<ItemStackTemplate> guaranteed = recipe.getGuaranteedOutput();
        List<Pair<ItemStackTemplate, Double>> chance = recipe.getChanceOutput();
        int outputX = startX + 70;
        int outputIdx = 0;

        for (ItemStackTemplate tpl : guaranteed) {
            int ox = outputX + (outputIdx % 2) * 22;
            int oy = recipeY + (outputIdx / 2) * 22;
            this.drawSlot(guiGraphics, ox, oy);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, tpl.create());
            outputIdx++;
        }

        for (Pair<ItemStackTemplate, Double> pair : chance) {
            if (outputIdx >= 4) break;
            int ox = outputX + (outputIdx % 2) * 22;
            int oy = recipeY + (outputIdx / 2) * 22;
            this.drawSlot(guiGraphics, ox, oy);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, pair.getFirst().create());
            String pct = String.format("%.0f%%", pair.getSecond() * 100);
            guiGraphics.text(this.font, pct, ox + 18, oy + 14, 0xFFAAAAAA, false);
            outputIdx++;
        }
    }
}
