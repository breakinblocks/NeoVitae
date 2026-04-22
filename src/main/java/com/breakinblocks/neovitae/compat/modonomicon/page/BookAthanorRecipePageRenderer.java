package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

import java.util.List;
import net.minecraft.world.item.ItemStackTemplate;

public class BookAthanorRecipePageRenderer extends BookRecipePageRenderer<AthanorRecipe, BookAthanorRecipePage> {

    private static final Identifier CRAFTING_TEXTURES = Identifier.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");
    private static final RenderPipeline GUI = RenderPipelines.GUI_TEXTURED;

    public BookAthanorRecipePageRenderer(BookAthanorRecipePage page) {
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

        RecipeHolder<AthanorRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        AthanorRecipe recipe = holder.value();

        List<Ingredient> inputs = recipe.getInputs();
        Ingredient primaryInput = inputs.isEmpty() ? Ingredient.of() : inputs.get(0);

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, recipeX + 2, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 5, recipeY + 3, mouseX, mouseY, primaryInput);

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, recipeX + 2, recipeY + 26, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, recipeX + 5, recipeY + 29, mouseX, mouseY, recipe.getTool());

        Component toolLabel = Component.literal("Tool");
        guiGraphics.text(this.font, toolLabel, recipeX + 26, recipeY + 33, 0xFF999999, false);

        guiGraphics.blit(GUI, CRAFTING_TEXTURES, recipeX + 50, recipeY + 2, 35, 198, 18, 18, 128, 256);

        List<ItemStackTemplate> guaranteed = recipe.getGuaranteedOutput();
        List<Pair<ItemStackTemplate, Double>> chance = recipe.getChanceOutput();
        int outputX = recipeX + 74;
        int outputIdx = 0;

        for (ItemStackTemplate tpl : guaranteed) {
            int ox = outputX + (outputIdx % 2) * 24;
            int oy = recipeY + (outputIdx / 2) * 24;
            guiGraphics.blit(GUI, CRAFTING_TEXTURES, ox, oy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, tpl.create());
            outputIdx++;
        }

        for (Pair<ItemStackTemplate, Double> pair : chance) {
            if (outputIdx >= 4) break;
            int ox = outputX + (outputIdx % 2) * 24;
            int oy = recipeY + (outputIdx / 2) * 24;
            guiGraphics.blit(GUI, CRAFTING_TEXTURES, ox, oy, 84, 198, 22, 22, 128, 256);
            this.parentScreen.renderItemStack(guiGraphics, ox + 3, oy + 3, mouseX, mouseY, pair.getFirst().create());
            String pct = String.format("%.0f%%", pair.getSecond() * 100);
            guiGraphics.text(this.font, pct, ox + 18, oy + 14, 0xFFAAAAAA, false);
            outputIdx++;
        }
    }
}
