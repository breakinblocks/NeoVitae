package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BloodTankUpgradeEmiRecipe extends BasicEmiRecipe {

    private static final int GRID_X = 1;
    private static final int GRID_Y = 1;
    private static final int ARROW_X = 60;
    private static final int OUTPUT_X = 82;
    private static final int ARROW_COLOR = 0xFF606060;

    public BloodTankUpgradeEmiRecipe(ItemStack inputTank, ItemStack outputTank, ResourceLocation id) {
        super(NVEmiCategories.BLOOD_TANK_UPGRADE, id, 102, 56);
        EmiStack glass = EmiStack.of(Items.GLASS);
        EmiStack bloodstone = EmiStack.of(NVBlocks.BLOODSTONE.block().get());
        EmiStack tank = EmiStack.of(inputTank);
        this.inputs = List.<EmiIngredient>of(
                glass, EmiStack.EMPTY, glass,
                tank, glass, tank,
                glass, bloodstone, glass);
        this.outputs = List.of(EmiStack.of(outputTank));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                EmiIngredient stack = inputs.get(row * 3 + col);
                if (stack.isEmpty()) {
                    widgets.addSlot(cellX(col), cellY(row));
                } else {
                    widgets.addSlot(stack, cellX(col), cellY(row));
                }
            }
        }
        widgets.addSlot(outputs.get(0), OUTPUT_X, cellY(1)).recipeContext(this);

        widgets.addDrawable(0, 0, 102, 56, (graphics, mouseX, mouseY, delta) -> {
            int ay = cellY(1) + 6;
            graphics.fill(ARROW_X, ay, ARROW_X + 16, ay + 2, ARROW_COLOR);
            graphics.fill(ARROW_X + 12, ay - 3, ARROW_X + 16, ay + 5, ARROW_COLOR);
            graphics.fill(ARROW_X + 13, ay - 2, ARROW_X + 16, ay + 4, ARROW_COLOR);
            graphics.fill(ARROW_X + 14, ay - 1, ARROW_X + 16, ay + 3, ARROW_COLOR);
        });
    }

    private static int cellX(int col) {
        return GRID_X + col * 18;
    }

    private static int cellY(int row) {
        return GRID_Y + row * 18;
    }
}
