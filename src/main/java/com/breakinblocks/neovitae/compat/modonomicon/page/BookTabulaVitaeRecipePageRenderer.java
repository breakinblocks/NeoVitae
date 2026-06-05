package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;

public class BookTabulaVitaeRecipePageRenderer extends BookNVRecipePageRenderer<TabulaVitaeRecipe, BookTabulaVitaeRecipePage> {

    public BookTabulaVitaeRecipePageRenderer(BookTabulaVitaeRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 68;
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

        RecipeHolder<TabulaVitaeRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        TabulaVitaeRecipe recipe = holder.value();

        List<Ingredient> ingredients = recipe.getInput();
        int cols = Math.min(ingredients.size(), 3);
        int rows = (ingredients.size() + 2) / 3;

        int slotSize = 18;
        int gridWidth = cols * slotSize;
        int totalWidth = gridWidth + 2 + 16 + 2 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        for (int i = 0; i < ingredients.size(); i++) {
            int col = i % 3;
            int row = i / 3;
            int slotX = startX + (col * slotSize);
            int slotY = recipeY + (row * slotSize);
            this.drawSlot(guiGraphics, slotX, slotY);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, slotY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + gridWidth + 2;
        int arrowY = recipeY + ((rows * slotSize - 16) / 2);
        this.drawArrow(guiGraphics, arrowX, arrowY);

        int outputX = arrowX + 20;
        int outputY = recipeY + ((rows * slotSize - 22) / 2);
        this.drawSlot(guiGraphics, outputX, outputY);
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, outputY + 3, mouseX, mouseY, recipe.getOutput());

        int textY = recipeY + (rows * slotSize) + 4;
        String lpFormatted = String.format("%,d", recipe.getSyphon());
        Component info = Component.literal(lpFormatted + " LP | " + (recipe.getTicks() / 20) + "s");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0xFF555555, 1.0f);
    }
}
