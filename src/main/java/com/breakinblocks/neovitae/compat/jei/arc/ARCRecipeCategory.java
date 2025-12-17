package com.breakinblocks.neovitae.compat.jei.arc;

import com.mojang.datafixers.util.Pair;
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
import com.breakinblocks.neovitae.common.recipe.arc.ARCRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * JEI recipe category for the Alchemical Reaction Chamber (ARC).
 * Displays ARC recipes including tool, input, and multiple outputs with chance percentages.
 */
public class ARCRecipeCategory implements IRecipeCategory<ARCRecipe> {
    public static final RecipeType<ARCRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "arc", ARCRecipe.class);

    private static final int WIDTH = 157;
    private static final int HEIGHT = 43;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public ARCRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(BMBlocks.ARC_BLOCK.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("gui/jei/arc.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.arc");
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
    public RecipeType<ARCRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ARCRecipe recipe, @Nonnull IFocusGroup focuses) {
        // Input slot (top left)
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 6);
        inputSlot.addIngredients(recipe.getInput());
        inputSlot.setSlotName("input");

        // Tool slot (below input)
        IRecipeSlotBuilder toolSlot = builder.addSlot(RecipeIngredientRole.CATALYST, 22, 17);
        toolSlot.addIngredients(recipe.getTool());
        toolSlot.setSlotName("tool");

        // Output slots - combine guaranteed and chanced outputs
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 54 + i * 22, 17);
            outputSlot.addItemStack(allOutputs.get(i).getFirst());
            outputSlot.setSlotName("output" + i);
        }
    }

    @Override
    public void draw(ARCRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw background
        background.draw(guiGraphics);

        Minecraft mc = Minecraft.getInstance();
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();

        // Draw chance percentages above output slots
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            double chance = allOutputs.get(i).getSecond();
            String chanceStr;

            if (chance >= 1.0) {
                chanceStr = ""; // Don't show anything for 100% chance
            } else if (chance < 0.01) {
                chanceStr = "<1%";
            } else {
                chanceStr = (int) Math.round(chance * 100) + "%";
            }

            if (!chanceStr.isEmpty()) {
                int x = 62 + i * 22 - mc.font.width(chanceStr) / 2;
                guiGraphics.drawString(mc.font, chanceStr, x, 5, Color.WHITE.getRGB(), true);
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ARCRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // Add tooltip for output chances when hovering over output area
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            int slotX = 54 + i * 22;
            int slotY = 17;

            if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                double chance = allOutputs.get(i).getSecond();
                if (chance < 1.0) {
                    tooltip.add(Component.translatable("jei.neovitae.recipe.arc.chance", (int) Math.round(chance * 100)));
                }
            }
        }
    }
}
