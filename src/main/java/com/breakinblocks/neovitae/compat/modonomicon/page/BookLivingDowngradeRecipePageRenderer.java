package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.client.event.ClientRecipeCache;
import com.breakinblocks.neovitae.common.recipe.livingdowngrade.LivingDowngradeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;

public class BookLivingDowngradeRecipePageRenderer extends BookRecipePageRenderer<LivingDowngradeRecipe, BookLivingDowngradeRecipePage> {

    private static final Identifier CRAFTING_TEXTURES = Identifier.fromNamespaceAndPath("modonomicon", "textures/gui/crafting_textures.png");
    private static final RenderPipeline GUI = RenderPipelines.GUI_TEXTURED;

    public BookLivingDowngradeRecipePageRenderer(BookLivingDowngradeRecipePage page) {
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

        RecipeHolder<LivingDowngradeRecipe> holder = ClientRecipeCache.byKey(
                second ? this.page.getRecipeKey2() : this.page.getRecipeKey1());
        if (holder == null) return;
        LivingDowngradeRecipe recipe = holder.value();

        int centerX = recipeX + BookEntryScreen.PAGE_WIDTH / 2;

        Component label = Component.literal("Downgrade Catalyst:");
        this.drawCenteredStringNoShadow(guiGraphics, label.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, recipeY, 0xFF555555, 1.0f);

        recipeY += 14;

        int slotX = centerX - 11;
        guiGraphics.blit(GUI, CRAFTING_TEXTURES, slotX, recipeY, 84, 198, 22, 22, 128, 256);
        this.parentScreen.renderIngredient(guiGraphics, slotX + 3, recipeY + 3, mouseX, mouseY, recipe.getInput());

        recipeY += 28;

        Component upgradeInfo = Component.literal("Target: " + recipe.getLivingUpgradeId().getPath());
        this.drawCenteredStringNoShadow(guiGraphics, upgradeInfo.getVisualOrderText(),
                BookEntryScreen.PAGE_WIDTH / 2, recipeY, 0xFF555555, 1.0f);
    }
}
