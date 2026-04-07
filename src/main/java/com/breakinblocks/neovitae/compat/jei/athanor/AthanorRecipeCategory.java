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
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AthanorRecipeCategory implements IRecipeCategory<AthanorRecipe> {
    public static final RecipeType<AthanorRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "athanor", AthanorRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 100;

    private static final SpiritusType[] TYPES = SpiritusType.values();
    private static final int[] TYPE_COLORS = {
            0xFFAA3333, 0xFF33AA33, 0xFFDD8822, 0xFF3355BB, 0xFFAA33CC
    };
    private static final String[] TYPE_NAMES = {"Raw", "Corrosive", "Destructive", "Steadfast", "Vengeful"};

    // Grid layout
    // Row 0 (y=2):  inputs col 0-2, tool, outputs col 0-1
    // Row 1 (y=20): inputs col 3-5,       outputs col 2-3
    // Row 2 (y=40): fluid_in,              fluid_out
    // Row 3 (y=56+): spiritus costs
    private static final int ROW0 = 2;
    private static final int ROW1 = 20;
    private static final int ROW2 = 40;

    private static final int INPUT_COL = 1;
    private static final int TOOL_COL = 60;
    private static final int ARROW_COL = 81;
    private static final int OUTPUT_COL = 102;

    @Nonnull private final IDrawable icon;
    @Nonnull private final IDrawable slot;

    public AthanorRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.ATHANOR_BLOCK.block().get()));
        slot = guiHelper.getSlotDrawable();
    }

    @Nonnull @Override public Component getTitle() { return Component.translatable("jei.neovitae.recipe.arc"); }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Nullable @Override public IDrawable getIcon() { return icon; }
    @Override public RecipeType<AthanorRecipe> getRecipeType() { return RECIPE_TYPE; }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull AthanorRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 6 inputs: 3x2 grid
        List<Ingredient> inputs = recipe.getInputs();
        for (int i = 0; i < inputs.size() && i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.INPUT,
                    INPUT_COL + 1 + col * 18, (row == 0 ? ROW0 : ROW1) + 1);
            s.addIngredients(inputs.get(i));
        }

        // Tool
        IRecipeSlotBuilder toolSlot = builder.addSlot(RecipeIngredientRole.CATALYST, TOOL_COL + 1, ROW0 + 10);
        toolSlot.addIngredients(recipe.getTool());

        // Outputs: 2x2 grid
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.OUTPUT,
                    OUTPUT_COL + 1 + col * 18, (row == 0 ? ROW0 : ROW1) + 1);
            s.addItemStack(allOutputs.get(i).getFirst());
        }

        // Fluid input
        recipe.getInputFluid().ifPresent(fluid -> {
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.INPUT, INPUT_COL + 1, ROW2 + 1);
            s.addFluidStack(fluid.getFluid(), fluid.getAmount());
            s.addRichTooltipCallback((v, t) -> t.add(Component.literal(fluid.getAmount() + " mB")));
        });

        // Fluid output
        recipe.getOutputFluid().ifPresent(fluid -> {
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_COL + 1, ROW2 + 1);
            s.addFluidStack(fluid.getFluid(), fluid.getAmount());
            s.addRichTooltipCallback((v, t) -> t.add(Component.literal(fluid.getAmount() + " mB")));
        });
    }

    @Override
    public void draw(AthanorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        // Input slot backgrounds (3x2)
        List<Ingredient> inputs = recipe.getInputs();
        for (int i = 0; i < inputs.size() && i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            slot.draw(guiGraphics, INPUT_COL + col * 18, row == 0 ? ROW0 : ROW1);
        }

        // Tool slot background
        slot.draw(guiGraphics, TOOL_COL, ROW0 + 9);

        // Arrow
        int ay = ROW0 + 14;
        guiGraphics.fill(ARROW_COL, ay, ARROW_COL + 16, ay + 2, 0xFF606060);
        guiGraphics.fill(ARROW_COL + 12, ay - 3, ARROW_COL + 16, ay + 5, 0xFF606060);
        guiGraphics.fill(ARROW_COL + 13, ay - 2, ARROW_COL + 16, ay + 4, 0xFF606060);
        guiGraphics.fill(ARROW_COL + 14, ay - 1, ARROW_COL + 16, ay + 3, 0xFF606060);

        // Output slot backgrounds (2x2)
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            slot.draw(guiGraphics, OUTPUT_COL + col * 18, row == 0 ? ROW0 : ROW1);
        }

        // Fluid slot backgrounds
        if (recipe.getInputFluid().isPresent()) {
            slot.draw(guiGraphics, INPUT_COL, ROW2);
        }
        if (recipe.getOutputFluid().isPresent()) {
            slot.draw(guiGraphics, OUTPUT_COL, ROW2);
        }

        // Labels
        guiGraphics.drawString(mc.font, "Tool", TOOL_COL, ROW0 + 28, 0x808080, false);

        // Spiritus costs
        if (recipe.hasSpiritusCosts()) {
            drawSpiritusCosts(guiGraphics, mc, recipe);
        }
    }

    private void drawSpiritusCosts(GuiGraphics guiGraphics, Minecraft mc, AthanorRecipe recipe) {
        Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
        int y = 58;

        guiGraphics.drawString(mc.font, Component.translatable("jei.neovitae.recipe.athanor.spiritus_cost"),
                1, y, 0xAAAAAA, true);
        y += 10;

        int col = 0;
        int baseX = 1;
        for (int i = 0; i < TYPES.length; i++) {
            Double amount = costs.get(TYPES[i]);
            if (amount == null || amount <= 0) continue;

            int x = baseX + col * 83;
            guiGraphics.fill(x, y + 1, x + 4, y + 5, TYPE_COLORS[i]);
            guiGraphics.drawString(mc.font, String.format("%.0f %s", amount, TYPE_NAMES[i]), x + 6, y, 0xFFFFFF, true);

            col++;
            if (col >= 2) {
                col = 0;
                y += 10;
            }
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AthanorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Pair<ItemStack, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            int sx = OUTPUT_COL + 1 + col * 18;
            int sy = (row == 0 ? ROW0 : ROW1) + 1;
            if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16) {
                double chance = allOutputs.get(i).getSecond();
                if (chance < 1.0) {
                    tooltip.add(Component.translatable("jei.neovitae.recipe.athanor.chance", (int) Math.round(chance * 100)));
                }
            }
        }

        if (recipe.hasSpiritusCosts() && mouseY >= 58) {
            Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
            int y = 68;
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
