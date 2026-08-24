package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import com.mojang.datafixers.util.Pair;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AthanorEmiRecipe extends BasicEmiRecipe {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 100;
    private static final int DROPLET = 81;
    private static final int ROW0 = 2;
    private static final int ROW1 = 20;
    private static final int ROW2 = 40;
    private static final int INPUT_COL = 1;
    private static final int TOOL_COL = 60;
    private static final int ARROW_COL = 81;
    private static final int OUTPUT_COL = 102;
    private static final int ARROW_COLOR = 0xFF606060;

    private final AthanorRecipe recipe;
    private final int itemInputCount;
    private final int itemOutputCount;

    public AthanorEmiRecipe(AthanorRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.ATHANOR, id, WIDTH, HEIGHT);
        this.recipe = recipe;

        List<EmiIngredient> ins = new ArrayList<>();
        List<Ingredient> itemInputs = recipe.getInputs();
        for (int i = 0; i < itemInputs.size() && i < 6; i++) {
            ins.add(EmiIngredient.of(itemInputs.get(i)));
        }
        this.itemInputCount = ins.size();
        recipe.getInputFluid().ifPresent(sized -> {
            List<EmiStack> fluids = new ArrayList<>();
            for (var fluidStack : sized.getFluids()) {
                fluids.add(EmiStack.of(fluidStack.getFluid(), (long) sized.amount() * DROPLET));
            }
            if (!fluids.isEmpty()) ins.add(EmiIngredient.of(fluids));
        });
        this.inputs = ins;

        List<EmiStack> outs = new ArrayList<>();
        List<Pair<ItemStack, Double>> listed = recipe.getAllListedOutputs();
        for (int i = 0; i < listed.size() && i < 4; i++) {
            outs.add(EmiStack.of(listed.get(i).getFirst()).setChance(listed.get(i).getSecond().floatValue()));
        }
        this.itemOutputCount = outs.size();
        recipe.getOutputFluid().ifPresent(fluid ->
                outs.add(EmiStack.of(fluid.getFluid(), (long) fluid.getAmount() * DROPLET)));
        this.outputs = outs;

        this.catalysts = List.of(EmiIngredient.of(recipe.getTool()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < itemInputCount; i++) {
            widgets.addSlot(inputs.get(i), INPUT_COL + (i % 3) * 18, (i / 3) == 0 ? ROW0 : ROW1);
        }
        if (inputs.size() > itemInputCount) {
            widgets.addSlot(inputs.get(itemInputCount), INPUT_COL, ROW2);
        }

        widgets.addSlot(catalysts.get(0), TOOL_COL, ROW0 + 9).catalyst(true);

        for (int i = 0; i < itemOutputCount; i++) {
            widgets.addSlot(outputs.get(i), OUTPUT_COL + (i % 2) * 18, (i / 2) == 0 ? ROW0 : ROW1)
                    .recipeContext(this);
        }
        if (outputs.size() > itemOutputCount) {
            widgets.addSlot(outputs.get(itemOutputCount), OUTPUT_COL, ROW2).recipeContext(this);
        }

        widgets.addText(Component.literal("Tool"), TOOL_COL, ROW0 + 28, 0x808080, false);

        widgets.addDrawable(0, 0, WIDTH, HEIGHT, (graphics, mouseX, mouseY, delta) -> {
            int ay = ROW0 + 14;
            graphics.fill(ARROW_COL, ay, ARROW_COL + 16, ay + 2, ARROW_COLOR);
            graphics.fill(ARROW_COL + 12, ay - 3, ARROW_COL + 16, ay + 5, ARROW_COLOR);
            graphics.fill(ARROW_COL + 13, ay - 2, ARROW_COL + 16, ay + 4, ARROW_COLOR);
            graphics.fill(ARROW_COL + 14, ay - 1, ARROW_COL + 16, ay + 3, ARROW_COLOR);

            Minecraft mc = Minecraft.getInstance();
            int infoY = 58;
            if (recipe.hasSpiritusCosts()) {
                infoY = drawSpiritusCosts(graphics, mc, infoY);
            }
            if (recipe.isSpiritusBoosted()) {
                drawSpiritusBoost(graphics, mc, infoY);
            }
        });
    }

    private int drawSpiritusCosts(net.minecraft.client.gui.GuiGraphics graphics, Minecraft mc, int startY) {
        Map<SpiritusType, Double> costs = recipe.getSpiritusCosts();
        int y = startY;
        graphics.drawString(mc.font, Component.translatable("jei.neovitae.recipe.athanor.spiritus_cost"), 1, y, 0xAAAAAA, true);
        y += 10;
        int col = 0;
        for (SpiritusType type : SpiritusType.values()) {
            Double amount = costs.get(type);
            if (amount == null || amount <= 0) continue;
            int x = 1 + col * 83;
            graphics.fill(x, y + 1, x + 4, y + 5, typeColor(type));
            graphics.drawString(mc.font, String.format("%.0f %s", amount, type.toCapitalized()), x + 6, y, 0xFFFFFF, true);
            col++;
            if (col >= 2) {
                col = 0;
                y += 10;
            }
        }
        return col == 0 ? y : y + 10;
    }

    private void drawSpiritusBoost(net.minecraft.client.gui.GuiGraphics graphics, Minecraft mc, int startY) {
        graphics.drawString(mc.font, Component.literal("Raw Spiritus Bonus"), 1, startY, 0xAAAAAA, true);
        graphics.fill(1, startY + 11, 5, startY + 15, typeColor(SpiritusType.RAW));
        int lineY = startY + 10;
        for (FormattedCharSequence line :
                mc.font.split(Component.literal("+1 output, scales 33%-100% (chunk 5-100 Raw)"), WIDTH - 8)) {
            graphics.drawString(mc.font, line, 7, lineY, 0xFFFFFF, true);
            lineY += mc.font.lineHeight;
        }
    }

    private static int typeColor(SpiritusType type) {
        return switch (type) {
            case RAW -> 0xFFAA3333;
            case RUINA -> 0xFF33AA33;
            case NIHILUM -> 0xFFDD8822;
            case INVICTUS -> 0xFF3355BB;
            case VINDICTA -> 0xFFAA33CC;
        };
    }
}
