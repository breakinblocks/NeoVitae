package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class BookSentientDowngradeRecipePageRenderer extends BookNVRecipePageRenderer<SentientDowngradeRecipe, BookSentientDowngradeRecipePage> {

    public BookSentientDowngradeRecipePageRenderer(BookSentientDowngradeRecipePage page) {
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

        RecipeHolder<SentientDowngradeRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        SentientDowngradeRecipe recipe = holder.value();

        int centerX = recipeX + BookEntryScreen.PAGE_WIDTH / 2;

        Component label = Component.literal("Downgrade Catalyst:");
        this.drawCenteredStringNoShadow(guiGraphics, label.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, recipeY, 0xFF555555, 1.0f);

        recipeY += 14;

        int slotX = centerX - 11;
        this.drawSlot(guiGraphics, slotX, recipeY);
        this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, recipe.getInput());

        recipeY += 28;

        Component upgradeInfo = Component.literal("Target: " + recipe.getSentientUpgradeId().getPath());
        this.drawCenteredStringNoShadow(guiGraphics, upgradeInfo.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, recipeY, 0xFF555555, 1.0f);
    }
}
