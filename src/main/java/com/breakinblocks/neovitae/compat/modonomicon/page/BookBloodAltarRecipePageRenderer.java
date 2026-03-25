package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BookBloodAltarRecipePageRenderer extends BookRecipePageRenderer<BloodAltarRecipe, BookBloodAltarRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookBloodAltarRecipePageRenderer(BookBloodAltarRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<BloodAltarRecipe> recipeHolder,
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

        guiGraphics.blit(CRAFTING_TEXTURES, recipeX + 13, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 16, recipeY + 3, mouseX, mouseY, recipe.getInput());

        guiGraphics.blit(CRAFTING_TEXTURES, recipeX + 50, recipeY + 2, 35, 198, 18, 18, 128, 256);

        guiGraphics.blit(CRAFTING_TEXTURES, recipeX + 79, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderItemStack(guiGraphics, recipeX + 82, recipeY + 3, mouseX, mouseY, recipe.getResult());

        int textY = recipeY + 28;
        int tier = recipe.getMinTier() + 1;
        String lpFormatted = String.format("%,d", recipe.getTotalBlood());
        Component info = Component.literal("Tier " + tier + " | " + lpFormatted + " LP");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0x555555, 1.0f);
    }
}
