package com.breakinblocks.neovitae.compat.jei.athanor;

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
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AthanorRecipeCategory implements IRecipeCategory<AthanorRecipe> {
    public static final RecipeType<AthanorRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "athanor", AthanorRecipe.class);

    private static final int WIDTH = 157;
    private static final int HEIGHT = 80;

    private static final SpiritusType[] TYPES = SpiritusType.values();
    private static final int[] TYPE_COLORS = {
            0xFFAA3333, // DEFAULT
            0xFF33AA33, // CORROSIVE
            0xFFDD8822, // DESTRUCTIVE
            0xFF3355BB, // STEADFAST
            0xFFAA33CC  // VENGEFUL
    };
    private static final String[] TYPE_NAMES = {"Raw", "Corrosive", "Destructive", "Steadfast", "Vengeful"};

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public AthanorRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.ATHANOR_BLOCK.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/athanor.png"), 0, 0, WIDTH, 43);
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
    public RecipeType<AthanorRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull AthanorRecipe recipe, @Nonnull IFocusGroup focuses) {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 6);
        inputSlot.addIngredients(recipe.getInput());
        inputSlot.setSlotName("input");

        IRecipeSlotBuilder toolSlot = builder.addSlot(RecipeIngredientRole.CATALYST, 22, 17);
        toolSlot.addIngredients(recipe.getTool());
        toolSlot.setSlotName("tool");

        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 54 + i * 22, 17);
            outputSlot.addItemStack(allOutputs.get(i).getFirst());
            outputSlot.setSlotName("output" + i);
        }

        recipe.getInputFluid().ifPresent(fluid -> {
            IRecipeSlotBuilder fluidIn = builder.addSlot(RecipeIngredientRole.INPUT, 1, 26);
            fluidIn.addFluidStack(fluid.getFluid(), fluid.getAmount());
            fluidIn.setSlotName("fluid_input");
            fluidIn.addRichTooltipCallback((view, tooltip) ->
                    tooltip.add(Component.literal(fluid.getAmount() + " mB")));
        });

        recipe.getOutputFluid().ifPresent(fluid -> {
            IRecipeSlotBuilder fluidOut = builder.addSlot(RecipeIngredientRole.OUTPUT, 140, 17);
            fluidOut.addFluidStack(fluid.getFluid(), fluid.getAmount());
            fluidOut.setSlotName("fluid_output");
            fluidOut.addRichTooltipCallback((view, tooltip) ->
                    tooltip.add(Component.literal(fluid.getAmount() + " mB")));
        });
    }

    @Override
    public void draw(AthanorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        Minecraft mc = Minecraft.getInstance();
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();

        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            double chance = allOutputs.get(i).getSecond();
            String chanceStr;

            if (chance >= 1.0) {
                chanceStr = "";
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

        if (recipe.hasSpiritusCosts()) {
            drawSpiritusCosts(guiGraphics, mc, recipe);
        }
    }

    private void drawSpiritusCosts(GuiGraphics guiGraphics, Minecraft mc, AthanorRecipe recipe) {
        Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
        int y = 46;

        guiGraphics.drawString(mc.font, Component.translatable("jei.neovitae.recipe.athanor.spiritus_cost"),
                1, y, 0xAAAAAA, true);
        y += 10;

        for (int i = 0; i < TYPES.length; i++) {
            Double amount = costs.get(TYPES[i]);
            if (amount == null || amount <= 0) continue;

            // Colored square
            guiGraphics.fill(1, y, 5, y + 4, TYPE_COLORS[i]);

            // Amount and type name
            String text = String.format("%.1f %s", amount, TYPE_NAMES[i]);
            guiGraphics.drawString(mc.font, text, 8, y - 1, 0xFFFFFF, true);
            y += 10;
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AthanorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            int slotX = 54 + i * 22;
            int slotY = 17;

            if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                double chance = allOutputs.get(i).getSecond();
                if (chance < 1.0) {
                    tooltip.add(Component.translatable("jei.neovitae.recipe.athanor.chance", (int) Math.round(chance * 100)));
                }
            }
        }

        if (recipe.hasSpiritusCosts() && mouseY >= 46 && mouseY <= HEIGHT) {
            Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
            int y = 56;
            for (int i = 0; i < TYPES.length; i++) {
                Double amount = costs.get(TYPES[i]);
                if (amount == null || amount <= 0) continue;

                if (mouseY >= y - 1 && mouseY < y + 9 && mouseX >= 0 && mouseX <= WIDTH) {
                    tooltip.add(Component.literal(String.format("Requires %.1f %s spiritus from the chunk", amount, TYPE_NAMES[i])));
                }
                y += 10;
            }
        }
    }
}
