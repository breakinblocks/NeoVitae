package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.breakinblocks.neovitae.compat.modonomicon.page.BookNVRecipePageRenderer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;

public class BookHellfireForgeRecipePageRenderer extends BookNVRecipePageRenderer<ForgeRecipe, BookHellfireForgeRecipePage> {

    private static final Identifier CRAFTING_TEXTURES = Identifier.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");
    private static final RenderPipeline GUI = RenderPipelines.GUI_TEXTURED;

    public BookHellfireForgeRecipePageRenderer(BookHellfireForgeRecipePage page) {
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

        RecipeHolder<ForgeRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        ForgeRecipe recipe = holder.value();

        List<Ingredient> ingredients = recipe.getCraftingIngredients();
        int count = Math.min(ingredients.size(), 4);
        int cols = Math.min(count, 3);
        int rows = (count + 2) / 3;

        int slotSize = 18;
        int gridWidth = cols * slotSize;
        int totalWidth = gridWidth + 2 + 16 + 2 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        for (int i = 0; i < count; i++) {
            int col = i % 3;
            int row = i / 3;
            int slotX = startX + (col * slotSize);
            int slotY = recipeY + (row * slotSize);
            guiGraphics.blit(GUI, CRAFTING_TEXTURES, slotX, slotY, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, slotY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + gridWidth + 2;
        int arrowY = recipeY + ((rows * slotSize - 16) / 2);
        guiGraphics.blit(GUI, CRAFTING_TEXTURES, arrowX, arrowY, 35, 198, 18, 18, 128, 256);

        int outputX = arrowX + 20;
        int outputY = recipeY + ((rows * slotSize - 22) / 2);
        guiGraphics.blit(GUI, CRAFTING_TEXTURES, outputX, outputY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, outputY + 3, mouseX, mouseY, recipe.getOutput());

        int textY = recipeY + (rows * slotSize) + 4;
        String willFormatted = String.format("%,.0f", recipe.getMinWill());
        String drainFormatted = String.format("%,.0f", recipe.getDrain());
        Component info = Component.literal("Will: " + willFormatted + " | Drain: " + drainFormatted);
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0xFF555555, 1.0f);
    }
}
