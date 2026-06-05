package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class BookAlchemyArrayRecipePageRenderer extends BookNVRecipePageRenderer<AlchemyArrayRecipe, BookAlchemyArrayRecipePage> {

    public BookAlchemyArrayRecipePageRenderer(BookAlchemyArrayRecipePage page) {
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

        RecipeHolder<AlchemyArrayRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        AlchemyArrayRecipe recipe = holder.value();

        int totalWidth = 22 + 7 + 22 + 16 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2 - 4;

        this.drawSlot(guiGraphics, startX, recipeY);
        this.parentScreen.renderIngredient(guiGraphics, startX + 3, recipeY + 3, mouseX, mouseY, recipe.getBaseInput());

        Component plus = Component.literal("+");
        this.drawCenteredStringNoShadow(guiGraphics, plus.getVisualOrderText(), startX + 25, recipeY + 6, 0xFF555555, 1.0f);

        this.drawSlot(guiGraphics, startX + 29, recipeY);
        this.parentScreen.renderIngredient(guiGraphics, startX + 32, recipeY + 3, mouseX, mouseY, recipe.getAddedInput());

        this.drawArrow(guiGraphics, startX + 53, recipeY + 2);

        this.drawSlot(guiGraphics, startX + 67, recipeY);
        this.parentScreen.renderItemStack(guiGraphics, startX + 70, recipeY + 3, mouseX, mouseY, recipe.getOutput());
    }
}
