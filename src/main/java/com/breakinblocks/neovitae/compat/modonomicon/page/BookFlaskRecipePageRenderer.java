package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
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

public class BookFlaskRecipePageRenderer extends BookNVRecipePageRenderer<FlaskRecipe, BookFlaskRecipePage> {

    private static final Identifier CRAFTING_TEXTURES = Identifier.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");
    private static final RenderPipeline GUI = RenderPipelines.GUI_TEXTURED;

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
            guiGraphics.blit(GUI, CRAFTING_TEXTURES, slotX, recipeY, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, ingredients.get(i));
        }

        int arrowX = startX + (count * 22) + 4;
        guiGraphics.blit(GUI, CRAFTING_TEXTURES, arrowX, recipeY + 2, 35, 198, 18, 18, 128, 256);

        int outputX = arrowX + 20;
        guiGraphics.blit(GUI, CRAFTING_TEXTURES, outputX, recipeY, 84, 198, 22, 22, 128, 256);
        var output = recipe.getOutput(recipe.getExampleFlask(), recipe.getExampleEffects());
        this.parentScreen.renderItemStack(guiGraphics, outputX + 3, recipeY + 3, mouseX, mouseY, output);

        int textY = recipeY + 26;
        int tier = recipe.getMinimumTier() + 1;
        String lpFormatted = String.format("%,d", recipe.getSyphon());
        Component info = Component.literal("Tier " + tier + " | " + lpFormatted + " LP | " + (recipe.getTicks() / 20) + "s");
        this.drawCenteredStringNoShadow(guiGraphics, info.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, textY, 0xFF555555, 1.0f);
    }
}
