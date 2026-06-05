package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class BookAraVitaeRecipePageRenderer extends BookNVRecipePageRenderer<AraVitaeRecipe, BookAraVitaeRecipePage> {

    public BookAraVitaeRecipePageRenderer(BookAraVitaeRecipePage page) {
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

        RecipeHolder<AraVitaeRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        AraVitaeRecipe recipe = holder.value();

        this.drawSlot(guiGraphics, recipeX + 13, recipeY);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 16, recipeY + 3, mouseX, mouseY, recipe.getInput());

        this.drawArrow(guiGraphics, recipeX + 50, recipeY + 2);

        this.drawSlot(guiGraphics, recipeX + 79, recipeY);
        this.parentScreen.renderItemStack(guiGraphics, recipeX + 82, recipeY + 3, mouseX, mouseY, recipe.getResult());

        int textY = recipeY + 28;
        int tier = recipe.getMinTier() + 1;
        String lpFormatted = String.format("%,d", recipe.getTotalBlood());
        Component info = Component.literal("Tier " + tier + " | " + lpFormatted + " LP");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0xFF555555, 1.0f);
    }
}
