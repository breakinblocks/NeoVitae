package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;

public class BookFlaskRecipePageRenderer extends BookNVRecipePageRenderer<FlaskRecipe, BookFlaskRecipePage> {

    public BookFlaskRecipePageRenderer(BookFlaskRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 58;
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

        RecipeHolder<FlaskRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        FlaskRecipe recipe = holder.value();

        List<Ingredient> ingredients = recipe.getInput();
        int count = ingredients.size();
        int totalWidth = (count * 22) + 4 + 18 + 2 + 22;
        int startX = recipeX + (BookEntryScreen.PAGE_WIDTH - totalWidth) / 2;

        for (int i = 0; i < count; i++) {
            int slotX = startX + (i * 22);
            this.drawSlot(guiGraphics, slotX, recipeY);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + (count * 22) + 4;
        this.drawArrow(guiGraphics, arrowX, recipeY + 2);

        int outputX = arrowX + 20;
        this.drawSlot(guiGraphics, outputX, recipeY);
        var output = recipe.getOutput(recipe.getExampleFlask(), recipe.getExampleEffects());
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, recipeY + 3, mouseX, mouseY, output);

        int textY = recipeY + 26;
        int tier = recipe.getMinimumTier();
        String lpFormatted = String.format("%,d", recipe.getSyphon());
        Component info = Component.literal("Tier " + tier + " | " + lpFormatted + " EV | " + (recipe.getTicks() / 20) + "s");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0xFF555555, 1.0f);
    }
}
