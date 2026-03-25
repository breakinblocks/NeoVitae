package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class BookSoulForgeRecipePageRenderer extends BookRecipePageRenderer<ForgeRecipe, BookSoulForgeRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookSoulForgeRecipePageRenderer(BookSoulForgeRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<ForgeRecipe> recipeHolder,
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

        List<Ingredient> ingredients = recipe.getCraftingIngredients();
        int count = Math.min(ingredients.size(), 4);
        int totalWidth = (count * 22) + 18 + 22 + 4;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        for (int i = 0; i < count; i++) {
            int slotX = startX + (i * 22);
            guiGraphics.blit(CRAFTING_TEXTURES, slotX, recipeY, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + (count * 22) + 2;
        guiGraphics.blit(CRAFTING_TEXTURES, arrowX, recipeY + 2, 35, 198, 18, 18, 128, 256);

        int outputX = arrowX + 20;
        guiGraphics.blit(CRAFTING_TEXTURES, outputX, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, recipeY + 3, mouseX, mouseY, recipe.getOutput());

        int textY = recipeY + 28;
        String willFormatted = String.format("%,.0f", recipe.getMinWill());
        String drainFormatted = String.format("%,.0f", recipe.getDrain());
        Component info = Component.literal("Will: " + willFormatted + " | Drain: " + drainFormatted);
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0x555555, 1.0f);
    }
}
