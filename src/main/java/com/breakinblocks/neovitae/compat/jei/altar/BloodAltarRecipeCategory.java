package com.breakinblocks.neovitae.compat.jei.altar;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;

public class BloodAltarRecipeCategory implements IRecipeCategory<BloodAltarRecipe> {

    public static final RecipeType<BloodAltarRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "blood_altar", BloodAltarRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI"};

    private static final int WIDTH = 155;
    private static final int HEIGHT = 65;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public BloodAltarRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(BMBlocks.BLOOD_ALTAR.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("gui/jei/altar.png"), 3, 4, WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<BloodAltarRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.altar");
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
    public void getTooltip(ITooltipBuilder tooltip, BloodAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 85 && mouseX <= 104 && mouseY >= 30 && mouseY <= 44) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.consumptionrate", DECIMAL_FORMAT.format(recipe.getCraftSpeed())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.drainrate", DECIMAL_FORMAT.format(recipe.getDrainSpeed())));
        }
    }

    @Override
    public void draw(BloodAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw background
        background.draw(guiGraphics);

        Minecraft mc = Minecraft.getInstance();
        String tierText = "Tier " + toRoman(recipe.getMinTier() + 1);
        String lpText = recipe.getTotalBlood() + " LP";

        guiGraphics.drawString(mc.font, tierText, 90 - mc.font.width(tierText) / 2, 0, Color.gray.getRGB(), false);
        guiGraphics.drawString(mc.font, lpText, 90 - mc.font.width(lpText) / 2, 10, Color.gray.getRGB(), false);

        // Show component transfer indicator
        if (recipe.shouldCopyInputComponents()) {
            String transferText = Component.translatable("jei.neovitae.recipe.componentTransfer").getString();
            guiGraphics.drawString(mc.font, transferText, 90 - mc.font.width(transferText) / 2, 55, new Color(100, 180, 100).getRGB(), false);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BloodAltarRecipe recipe, IFocusGroup focuses) {
        // Output
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 31);
        output.addItemStack(recipe.getResult());

        // Input
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 32, 1);
        input.addIngredients(recipe.getInput());
    }

    private static String toRoman(int number) {
        if (number >= 1 && number <= ROMAN_NUMERALS.length) {
            return ROMAN_NUMERALS[number - 1];
        }
        return String.valueOf(number);
    }
}
