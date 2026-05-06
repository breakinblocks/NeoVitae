package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BookSentientDowngradeRecipePageRenderer extends BookRecipePageRenderer<SentientDowngradeRecipe, BookSentientDowngradeRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookSentientDowngradeRecipePageRenderer(BookSentientDowngradeRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<SentientDowngradeRecipe> recipeHolder,
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

        int centerX = recipeX + BookEntryScreen.PAGE_WIDTH / 2;

        Component label = Component.literal("Downgrade Catalyst:");
        this.drawCenteredStringNoShadow(guiGraphics, label.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, recipeY, 0x555555, 1.0f);

        recipeY += 14;

        int slotX = centerX - 11;
        guiGraphics.blit(CRAFTING_TEXTURES, slotX, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, recipe.getInput());

        recipeY += 28;

        Component upgradeInfo = Component.literal("Target: " + recipe.getLivingUpgradeId().getPath());
        this.drawCenteredStringNoShadow(guiGraphics, upgradeInfo.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, recipeY, 0x555555, 1.0f);
    }
}
