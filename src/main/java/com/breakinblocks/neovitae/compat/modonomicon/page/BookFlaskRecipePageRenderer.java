package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class BookFlaskRecipePageRenderer extends BookRecipePageRenderer<FlaskRecipe, BookFlaskRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookFlaskRecipePageRenderer(BookFlaskRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 58;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<FlaskRecipe> recipeHolder,
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

        // Flask recipes have 1-2 ingredients — use compact horizontal layout
        List<Ingredient> ingredients = recipe.getInput();
        int count = ingredients.size();
        int totalWidth = (count * 22) + 4 + 18 + 2 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        for (int i = 0; i < count; i++) {
            int slotX = startX + (i * 22);
            guiGraphics.blit(CRAFTING_TEXTURES, slotX, recipeY, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + (count * 22) + 4;
        guiGraphics.blit(CRAFTING_TEXTURES, arrowX, recipeY + 2, 35, 198, 18, 18, 128, 256);

        int outputX = arrowX + 20;
        guiGraphics.blit(CRAFTING_TEXTURES, outputX, recipeY, 84, 198, 22, 22, 128, 256);
        var output = recipe.getOutput(recipe.getExampleFlask(), recipe.getExampleEffects());
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, recipeY + 3, mouseX, mouseY, output);

        int textY = recipeY + 26;
        int tier = recipe.getMinimumTier() + 1;
        String lpFormatted = String.format("%,d", recipe.getSyphon());
        Component info = Component.literal("Tier " + tier + " | " + lpFormatted + " LP | " + (recipe.getTicks() / 20) + "s");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0x555555, 1.0f);
    }
}
