package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Bypasses the parent's null-check on {@code RecipeDisplayEntry}. Our custom recipe types
 * don't implement {@code Recipe#display()} (added in 1.21.4), so the server-side
 * book bake leaves the page's display entries null and the parent renderer would draw
 * the red "Recipe not found" message. Our subclasses fetch the recipe themselves via
 * {@link com.breakinblocks.neovitae.client.event.ClientRecipeCache} in {@code drawRecipe},
 * so we can safely call drawRecipe with a null entry instead.
 */
public abstract class BookNVRecipePageRenderer<R extends Recipe<?>, T extends BookRecipePage<R>>
        extends BookRecipePageRenderer<R, T> {

    public BookNVRecipePageRenderer(T page) {
        super(page);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int recipeX = X;
        int recipeY = Y;

        if (this.page.getRecipeKey1() != null) {
            this.drawRecipe(guiGraphics, null, recipeX, recipeY, mouseX, mouseY, false);
            if (this.page.getRecipeKey2() != null) {
                int offset = recipeY + this.getRecipeHeight()
                        - (this.page.getTitle2().getString().isEmpty() ? 0 : 10);
                this.drawRecipe(guiGraphics, null, recipeX, offset, mouseX, mouseY, true);
            }
        }

        int textY = this.getTextY();
        this.renderBookTextHolder(guiGraphics, this.page.getText(), 0, textY, 124, 155 - textY);

        Style style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            this.parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY);
        }
    }
}
