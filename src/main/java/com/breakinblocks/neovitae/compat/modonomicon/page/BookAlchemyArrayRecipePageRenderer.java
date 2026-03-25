package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BookAlchemyArrayRecipePageRenderer extends BookRecipePageRenderer<AlchemyArrayRecipe, BookAlchemyArrayRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookAlchemyArrayRecipePageRenderer(BookAlchemyArrayRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<AlchemyArrayRecipe> recipeHolder,
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

        int totalWidth = 22 + 9 + 22 + 18 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        guiGraphics.blit(CRAFTING_TEXTURES, startX, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, startX + 3, recipeY + 3, mouseX, mouseY, recipe.getBaseInput());

        Component plus = Component.literal("+");
        this.drawCenteredStringNoShadow(guiGraphics, plus.getVisualOrderText(), startX + 26, recipeY + 6, 0x555555, 1.0f);

        guiGraphics.blit(CRAFTING_TEXTURES, startX + 31, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, startX + 34, recipeY + 3, mouseX, mouseY, recipe.getAddedInput());

        guiGraphics.blit(CRAFTING_TEXTURES, startX + 57, recipeY + 2, 35, 198, 18, 18, 128, 256);

        guiGraphics.blit(CRAFTING_TEXTURES, startX + 71, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderItemStack(guiGraphics, startX + 74, recipeY + 3, mouseX, mouseY, recipe.getOutput());
    }
}
