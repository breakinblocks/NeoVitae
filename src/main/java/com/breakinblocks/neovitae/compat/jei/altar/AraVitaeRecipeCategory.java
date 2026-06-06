// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2020-2023 MystChonky <https://github.com/dphaldes>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.compat.jei.altar;

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
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;

public class AraVitaeRecipeCategory implements IRecipeCategory<AraVitaeRecipe> {

    public static final IRecipeType<AraVitaeRecipe> RECIPE_TYPE = IRecipeType.create(NeoVitae.MODID, "ara_vitae", AraVitaeRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI"};

    private static final int WIDTH = 155;
    private static final int HEIGHT = 65;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public AraVitaeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.ARA_VITAE.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/altar.png"), 3, 4, WIDTH, HEIGHT);
    }

    @Override
    public IRecipeType<AraVitaeRecipe> getRecipeType() {
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
    public void getTooltip(ITooltipBuilder tooltip, AraVitaeRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 85 && mouseX <= 104 && mouseY >= 30 && mouseY <= 44) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.consumptionrate", DECIMAL_FORMAT.format(recipe.getCraftSpeed())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.drainrate", DECIMAL_FORMAT.format(recipe.getDrainSpeed())));
        }
    }

    @Override
    public void draw(AraVitaeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        Minecraft mc = Minecraft.getInstance();
        String tierText = "Tier " + recipe.getMinTier();
        String lpText = recipe.getTotalBlood() + " EV";

        guiGraphics.text(mc.font, tierText, 90 - mc.font.width(tierText) / 2, 0, Color.gray.getRGB());
        guiGraphics.text(mc.font, lpText, 90 - mc.font.width(lpText) / 2, 10, Color.gray.getRGB());

        if (recipe.shouldCopyInputComponents()) {
            String transferText = Component.translatable("jei.neovitae.recipe.componentTransfer").getString();
            guiGraphics.text(mc.font, transferText, 90 - mc.font.width(transferText) / 2, 63, new Color(100, 180, 100).getRGB());
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AraVitaeRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 31);
        output.add(recipe.getResult());

        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 32, 1);
        input.add(recipe.getInput());
    }

    private static String toRoman(int number) {
        if (number >= 1 && number <= ROMAN_NUMERALS.length) {
            return ROMAN_NUMERALS[number - 1];
        }
        return String.valueOf(number);
    }
}
