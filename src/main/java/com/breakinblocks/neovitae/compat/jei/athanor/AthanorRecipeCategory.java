package com.breakinblocks.neovitae.compat.jei.athanor;

import com.mojang.datafixers.util.Pair;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.compat.jei.NVJeiRecipeIds;

public class AthanorRecipeCategory implements IRecipeCategory<AthanorRecipe> {
    public static final IRecipeType<AthanorRecipe> RECIPE_TYPE = IRecipeType.create(NeoVitae.MODID, "athanor", AthanorRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 100;

    private static final SpiritusType[] TYPES = SpiritusType.values();

    private static int typeColor(SpiritusType type) {
        return switch (type) {
            case RAW -> 0xFFAA3333;
            case RUINA -> 0xFF33AA33;
            case NIHILUM -> 0xFFDD8822;
            case INVICTUS -> 0xFF3355BB;
            case VINDICTA -> 0xFFAA33CC;
        };
    }

    private static Component typeName(SpiritusType type) {
        return Component.translatable("spiritus.neovitae." + type.getSerializedName());
    }

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
    @Override public IRecipeType<AthanorRecipe> getRecipeType() { return RECIPE_TYPE; }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull AthanorRecipe recipe, @Nonnull IFocusGroup focuses) {
        // 6 inputs: 3x2 grid
        List<Ingredient> inputs = recipe.getInputs();
        for (int i = 0; i < inputs.size() && i < 6; i++) {
            int col = i % 3;
            int row = i / 3;
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.INPUT,
                    INPUT_COL + 1 + col * 18, (row == 0 ? ROW0 : ROW1) + 1);
            s.add(inputs.get(i));
        }

        // Tool
        IRecipeSlotBuilder toolSlot = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, TOOL_COL + 1, ROW0 + 10);
        toolSlot.add(recipe.getTool());

        // Outputs: 2x2 grid
        List<Pair<ItemStackTemplate, Double>> allOutputs = recipe.getAllListedOutputs();
        for (int i = 0; i < allOutputs.size() && i < 4; i++) {
            int col = i % 2;
            int row = i / 2;
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.OUTPUT,
                    OUTPUT_COL + 1 + col * 18, (row == 0 ? ROW0 : ROW1) + 1);
            s.add(allOutputs.get(i).getFirst().create());
        }

        // Fluid input
        recipe.getInputFluid().ifPresent(sized -> {
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.INPUT, INPUT_COL + 1, ROW2 + 1);
            int amount = sized.amount();
            List<FluidStack> stacks = sized.ingredient().fluids().stream()
                    .map(h -> new FluidStack(h, amount))
                    .toList();
            s.addIngredients(NeoForgeTypes.FLUID_STACK, stacks);
            s.addRichTooltipCallback((v, t) -> t.add(Component.translatable("jei.neovitae.athanor.mb", amount)));
        });

        // Fluid output
        recipe.getOutputFluid().ifPresent(fluid -> {
            IRecipeSlotBuilder s = builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_COL + 1, ROW2 + 1);
            s.add(fluid.getFluid(), fluid.getAmount());
            s.addRichTooltipCallback((v, t) -> t.add(Component.translatable("jei.neovitae.athanor.mb", fluid.getAmount())));
        });
    }

    @Override
    public void draw(AthanorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
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
        List<Pair<ItemStackTemplate, Double>> allOutputs = recipe.getAllListedOutputs();
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
        guiGraphics.text(mc.font, "Tool", TOOL_COL, ROW0 + 28, 0xFF808080, false);

        // Spiritus costs
        int infoY = 58;
        if (recipe.hasSpiritusCosts()) {
            infoY = drawSpiritusCosts(guiGraphics, mc, recipe, infoY);
        }
        if (recipe.isSpiritusBoosted()) {
            drawSpiritusBoost(guiGraphics, mc, infoY);
        }
    }

    private int drawSpiritusCosts(GuiGraphicsExtractor guiGraphics, Minecraft mc, AthanorRecipe recipe, int startY) {
        Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
        int y = startY;

        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.recipe.athanor.spiritus_cost"),
                1, y, 0xFFAAAAAA);
        y += 10;

        int col = 0;
        int baseX = 1;
        for (SpiritusType type : TYPES) {
            Double amount = costs.get(type);
            if (amount == null || amount <= 0) continue;

            int x = baseX + col * 83;
            guiGraphics.fill(x, y + 1, x + 4, y + 5, typeColor(type));
            guiGraphics.text(mc.font, String.format("%.0f %s", amount, type.toCapitalized()), x + 6, y, 0xFFFFFFFF);

            col++;
            if (col >= 2) {
                col = 0;
                y += 10;
            }
        }
        return col == 0 ? y : y + 10;
    }

    private void drawSpiritusBoost(GuiGraphicsExtractor guiGraphics, Minecraft mc, int startY) {
        guiGraphics.text(mc.font, Component.literal("Raw Spiritus Bonus"), 1, startY, 0xFFAAAAAA);
        guiGraphics.fill(1, startY + 11, 5, startY + 15, typeColor(SpiritusType.RAW));
        int textX = 7;
        int textMaxWidth = WIDTH - textX - 1;
        int lineY = startY + 10;
        for (FormattedCharSequence line :
                mc.font.split(Component.literal("+1 output, scales 33%-100% (chunk 5-100 Raw)"), textMaxWidth)) {
            guiGraphics.text(mc.font, line, textX, lineY, 0xFFFFFFFF);
            lineY += mc.font.lineHeight;
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AthanorRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Pair<ItemStackTemplate, Double>> allOutputs = recipe.getAllListedOutputs();
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

        int infoY = 58;
        if (recipe.hasSpiritusCosts() && mouseY >= 58) {
            Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
            int y = 68;
            int col = 0;
            for (SpiritusType type : TYPES) {
                Double amount = costs.get(type);
                if (amount == null || amount <= 0) continue;
                if (mouseY >= y - 1 && mouseY < y + 9 && mouseX >= 0 && mouseX <= WIDTH) {
                    tooltip.add(Component.translatable("jei.neovitae.athanor.spiritus_requirement", String.format("%.1f", amount), typeName(type)));
                }
                col++;
                if (col >= 2) {
                    col = 0;
                    y += 10;
                }
            }
            infoY = col == 0 ? y : y + 10;
        }

        if (recipe.isSpiritusBoosted() && mouseX >= 0 && mouseX <= WIDTH
                && mouseY >= infoY && mouseY < infoY + 20) {
            tooltip.add(Component.literal("Spiritus Boost"));
            tooltip.add(Component.literal("+1 of the first output when Raw Spiritus saturates the chunk."));
            tooltip.add(Component.literal("Chance: 33% at 5 Raw, scaling linearly to 100% at 100 Raw."));
            tooltip.add(Component.literal("Multiplied by the tool's bonus-chance modifier. Any whole multiples"));
            tooltip.add(Component.literal("guarantee one extra output each, with the remainder rolled separately."));
            tooltip.add(Component.literal("Each bonus has a 2.5% chance to consume 1 Raw Spiritus."));
        }
    }

    @Override
    public Identifier getIdentifier(AthanorRecipe recipe) {
        return NVJeiRecipeIds.get(recipe);
    }
}
