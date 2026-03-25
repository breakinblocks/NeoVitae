package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class BookARCRecipePageRenderer extends BookRecipePageRenderer<ARCRecipe, BookARCRecipePage> {

    private static final ResourceLocation CRAFTING_TEXTURES = ResourceLocation.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");

    public BookARCRecipePageRenderer(BookARCRecipePage page) {
        super(page);
    }

    @Override
    protected int getRecipeHeight() {
        return 78;
    }

    @Override
    protected void drawRecipe(GuiGraphics guiGraphics, RecipeHolder<ARCRecipe> recipeHolder,
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

        guiGraphics.blit(CRAFTING_TEXTURES, recipeX + 2, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 5, recipeY + 3, mouseX, mouseY, recipe.getInput());

        guiGraphics.blit(CRAFTING_TEXTURES, recipeX + 2, recipeY + 26, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 5, recipeY + 29, mouseX, mouseY, recipe.getTool());

        Component toolLabel = Component.literal("Tool");
        guiGraphics.drawString(this.font, toolLabel, recipeX + 26, recipeY + 33, 0x999999, false);

        guiGraphics.blit(CRAFTING_TEXTURES, recipeX + 50, recipeY + 2, 35, 198, 18, 18, 128, 256);

        List<ItemStack> guaranteed = recipe.getGuaranteedOutput();
        List<Pair<ItemStack, Double>> chance = recipe.getChanceOutput();
        int outputX = recipeX + 74;
        int outputIdx = 0;

        for (ItemStack stack : guaranteed) {
            int ox = outputX + (outputIdx % 2) * 24;
            int oy = recipeY + (outputIdx / 2) * 24;
            guiGraphics.blit(CRAFTING_TEXTURES, ox, oy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, stack);
            outputIdx++;
        }

        for (Pair<ItemStack, Double> pair : chance) {
            if (outputIdx >= 4) break;
            int ox = outputX + (outputIdx % 2) * 24;
            int oy = recipeY + (outputIdx / 2) * 24;
            guiGraphics.blit(CRAFTING_TEXTURES, ox, oy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, pair.getFirst());
            String pct = String.format("%.0f%%", pair.getSecond() * 100);
            guiGraphics.drawString(this.font, pct, ox + 18, oy + 14, 0xAAAAAA, false);
            outputIdx++;
        }
    }
}
