package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class BookTabulaVitaeRecipePageRenderer extends BookRecipePageRenderer<TabulaVitaeRecipe, BookTabulaVitaeRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookTabulaVitaeRecipePageRenderer(BookTabulaVitaeRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 68;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<TabulaVitaeRecipe> recipeHolder,
                              int recipeX, int recipeY, int mouseX, int mouseY, boolean second) {
        var recipe = recipeHolder.value();

        if (!second) {
            if (!this.page.getTitle1().isEmpty())
                this.renderTitle(guiGraphics, this.page.getTitle1(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
        } else {
            if (!this.page.getTitle2().isEmpty())
                this.renderTitle(guiGraphics, this.page.getTitle2(), false, BookEntryScreen.PAGE_WIDTH / 2, recipeY - 10);
        }

        recipeY += 8;

        List<Ingredient> ingredients = recipe.getInput();
        int cols = Math.min(ingredients.size(), 3);
        int rows = (ingredients.size() + 2) / 3;

        // Use 18px slot spacing for compact grid + horizontal arrow + output
        int slotSize = 18;
        int gridWidth = cols * slotSize;
        int totalWidth = gridWidth + 2 + 16 + 2 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        for (int i = 0; i < ingredients.size(); i++) {
            int col = i % 3;
            int row = i / 3;
            int slotX = startX + (col * slotSize);
            int slotY = recipeY + (row * slotSize);
            guiGraphics.blit(CRAFTING_TEXTURES, slotX, slotY, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, slotY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + gridWidth + 2;
        int arrowY = recipeY + ((rows * slotSize - 16) / 2);
        guiGraphics.blit(CRAFTING_TEXTURES, arrowX, arrowY, 35, 198, 18, 18, 128, 256);

        int outputX = arrowX + 20;
        int outputY = recipeY + ((rows * slotSize - 22) / 2);
        guiGraphics.blit(CRAFTING_TEXTURES, outputX, outputY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, outputY + 3, mouseX, mouseY, recipe.getOutput());

        int textY = recipeY + (rows * slotSize) + 4;
        String lpFormatted = String.format("%,d", recipe.getSyphon());
        Component info = Component.literal(lpFormatted + " LP | " + (recipe.getTicks() / 20) + "s");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0x555555, 1.0f);
    }
}
