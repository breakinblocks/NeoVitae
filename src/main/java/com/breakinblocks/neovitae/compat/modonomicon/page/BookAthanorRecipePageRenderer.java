package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class BookAthanorRecipePageRenderer extends BookRecipePageRenderer<AthanorRecipe, BookAthanorRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookAthanorRecipePageRenderer(BookAthanorRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<AthanorRecipe> recipeHolder,
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

        List<Ingredient> inputs = recipe.getInputs();
        int outputCount = recipe.getGuaranteedOutput().size() + recipe.getChanceOutput().size();
        int outputWidth = outputCount > 1 ? 44 : 22;
        int startX = (BookEntryScreen.PAGE_WIDTH - (44 + 26 + outputWidth)) / 2;
        int toolX = startX + 46;
        for (int idx = 0; idx < inputs.size() && idx < 6; idx++) {
            int col = idx % 2;
            int row = idx / 2;
            int ix = startX + col * 22;
            int iy = recipeY + row * 22;
            guiGraphics.blit(CRAFTING_TEXTURES, ix, iy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderIngredient(guiGraphics, ix + 3, iy + 3, mouseX, mouseY, inputs.get(idx));
        }

        guiGraphics.blit(CRAFTING_TEXTURES, toolX, recipeY + 38, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, toolX + 3, recipeY + 41, mouseX, mouseY, recipe.getTool());

        Component toolLabel = Component.literal("Tool");
        guiGraphics.drawString(this.font, toolLabel, toolX, recipeY + 62, 0x999999, false);

        guiGraphics.blit(CRAFTING_TEXTURES, toolX + 2, recipeY + 10, 35, 198, 18, 18, 128, 256);

        List<ItemStack> guaranteed = recipe.getGuaranteedOutput();
        List<Pair<ItemStack, Double>> chance = recipe.getChanceOutput();
        int outputX = startX + 70;
        int outputIdx = 0;

        for (ItemStack stack : guaranteed) {
            int ox = outputX + (outputIdx % 2) * 22;
            int oy = recipeY + (outputIdx / 2) * 22;
            guiGraphics.blit(CRAFTING_TEXTURES, ox, oy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, stack);
            outputIdx++;
        }

        for (Pair<ItemStack, Double> pair : chance) {
            if (outputIdx >= 4) break;
            int ox = outputX + (outputIdx % 2) * 22;
            int oy = recipeY + (outputIdx / 2) * 22;
            guiGraphics.blit(CRAFTING_TEXTURES, ox, oy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, pair.getFirst());
            String pct = String.format("%.0f%%", pair.getSecond() * 100);
            guiGraphics.drawString(this.font, pct, ox + 18, oy + 14, 0xAAAAAA, false);
            outputIdx++;
        }
    }
}
