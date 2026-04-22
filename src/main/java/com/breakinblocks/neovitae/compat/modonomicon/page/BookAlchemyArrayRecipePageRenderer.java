package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class BookAlchemyArrayRecipePageRenderer extends BookRecipePageRenderer<AlchemyArrayRecipe, BookAlchemyArrayRecipePage> {

    private static final Identifier CRAFTING_TEXTURES = Identifier.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");
    private static final RenderPipeline GUI = RenderPipelines.GUI_TEXTURED;

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

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, startX, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, startX + 3, recipeY + 3, mouseX, mouseY, recipe.getBaseInput());

        Component plus = Component.literal("+");
        this.drawCenteredStringNoShadow(guiGraphics, plus.getVisualOrderText(), startX + 25, recipeY + 6, 0xFF555555, 1.0f);

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, startX + 29, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, startX + 32, recipeY + 3, mouseX, mouseY, recipe.getAddedInput());

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, startX + 53, recipeY + 2, 35, 198, 18, 18, 128, 256);

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, startX + 67, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderItemStack(guiGraphics, startX + 70, recipeY + 3, mouseX, mouseY, recipe.getOutput());
    }
}
