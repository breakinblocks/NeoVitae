package com.breakinblocks.neovitae.compat.jei.meteor;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import com.breakinblocks.neovitae.compat.jei.NVJeiRecipeIds;
import com.breakinblocks.neovitae.compat.viewer.MeteorOutputEstimator;

/**
 * JEI recipe category for meteor ritual recipes.
 * Displays the catalyst input, all possible output blocks with weights, and recipe stats.
 */
public class MeteorRecipeCategory implements IRecipeCategory<MeteorRecipe> {

    public static final IRecipeType<MeteorRecipe> RECIPE_TYPE = IRecipeType.create(NeoVitae.MODID, "meteor", MeteorRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    private static final int WIDTH = 170;
    private static final int HEIGHT = 120;

    private final IDrawable icon;

    public MeteorRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()));
    }

    @Override
    public IRecipeType<MeteorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.meteor");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(MeteorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        String costText = "Cost: " + DECIMAL_FORMAT.format(recipe.getSyphon()) + " EV";
        guiGraphics.text(mc.font, costText, 30, 3, Color.GRAY.getRGB(), false);

        String explosionText = "Explosion: " + recipe.getExplosionRadius();
        guiGraphics.text(mc.font, explosionText, 30, 13, Color.GRAY.getRGB(), false);

        int diameter = MeteorOutputEstimator.maxRadius(recipe) * 2 + 1;
        String sizeText = "Size: " + diameter + " Blocks";
        guiGraphics.text(mc.font, sizeText, 30, 23, Color.GRAY.getRGB(), false);

        // Draw "Catalyst:" label
        guiGraphics.text(mc.font, "Catalyst:", 0, 40, Color.DARK_GRAY.getRGB(), false);

        // Draw "Outputs:" label
        guiGraphics.text(mc.font, "Outputs:", 0, 58, Color.DARK_GRAY.getRGB(), false);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MeteorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeteorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 50, 38)
                .add(recipe.getInput());

        List<MeteorOutputEstimator.Estimate> estimates = MeteorOutputEstimator.estimate(recipe);
        int slotsPerRow = 8;
        int startX = 5;
        int startY = 70;

        for (int index = 0; index < estimates.size(); index++) {
            MeteorOutputEstimator.Estimate estimate = estimates.get(index);
            int x = startX + (index % slotsPerRow) * 18;
            int y = startY + (index / slotsPerRow) * 18;

            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);
            slot.addItemStacks(estimate.stacks());

            int estimatedCount = estimate.count();
            double percentage = estimate.percentage();
            int poolSize = estimate.poolSize();
            slot.addRichTooltipCallback((view, tooltipBuilder) -> {
                tooltipBuilder.add(Component.translatable("jei.neovitae.recipe.meteor.estimate",
                        DECIMAL_FORMAT.format(estimatedCount), String.format("%.1f", percentage)));
                if (poolSize > 1) {
                    tooltipBuilder.add(Component.translatable("jei.neovitae.recipe.meteor.random_pool",
                            DECIMAL_FORMAT.format(poolSize)));
                }
            });
        }
    }

    @Override
    public Identifier getIdentifier(MeteorRecipe recipe) {
        return NVJeiRecipeIds.get(recipe);
    }
}
