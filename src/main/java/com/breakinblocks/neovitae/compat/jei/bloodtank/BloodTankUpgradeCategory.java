package com.breakinblocks.neovitae.compat.jei.bloodtank;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.compat.jei.NVJeiRecipeIds;

public class BloodTankUpgradeCategory implements IRecipeCategory<BloodTankUpgradeJEIRecipe> {
    public static final RecipeType<BloodTankUpgradeJEIRecipe> RECIPE_TYPE =
            RecipeType.create(NeoVitae.MODID, "blood_tank_upgrade", BloodTankUpgradeJEIRecipe.class);

    private static final int GRID_X = 1;
    private static final int GRID_Y = 1;
    private static final int ARROW_X = 60;
    private static final int OUTPUT_X = 82;
    private static final int WIDTH = 102;
    private static final int HEIGHT = 56;

    @Nonnull private final IDrawable icon;
    @Nonnull private final IDrawable slot;
    private final ItemStack glass = new ItemStack(Items.GLASS);
    private final ItemStack bloodstone;

    public BloodTankUpgradeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.BLOOD_TANK.block().get()));
        slot = guiHelper.getSlotDrawable();
        bloodstone = new ItemStack(NVBlocks.BLOODSTONE.block().get());
    }

    @Nonnull @Override public Component getTitle() { return Component.translatable("jei.neovitae.recipe.blood_tank_upgrade"); }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Nullable @Override public IDrawable getIcon() { return icon; }
    @Override public RecipeType<BloodTankUpgradeJEIRecipe> getRecipeType() { return RECIPE_TYPE; }

    private int cellX(int col) { return GRID_X + col * 18; }
    private int cellY(int row) { return GRID_Y + row * 18; }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull BloodTankUpgradeJEIRecipe recipe, @Nonnull IFocusGroup focuses) {
        addInput(builder, 0, 0, glass);
        addInput(builder, 2, 0, glass);
        addInput(builder, 0, 1, recipe.inputTank());
        addInput(builder, 1, 1, glass);
        addInput(builder, 2, 1, recipe.inputTank());
        addInput(builder, 0, 2, glass);
        addInput(builder, 1, 2, bloodstone);
        addInput(builder, 2, 2, glass);

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, cellY(1) + 1).addItemStack(recipe.outputTank());
    }

    private void addInput(IRecipeLayoutBuilder builder, int col, int row, ItemStack stack) {
        builder.addSlot(RecipeIngredientRole.INPUT, cellX(col) + 1, cellY(row) + 1).addItemStack(stack);
    }

    @Override
    public void draw(BloodTankUpgradeJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                slot.draw(guiGraphics, cellX(col), cellY(row));
            }
        }
        slot.draw(guiGraphics, OUTPUT_X, cellY(1));

        int ay = cellY(1) + 5;
        guiGraphics.fill(ARROW_X, ay, ARROW_X + 16, ay + 2, 0xFF606060);
        guiGraphics.fill(ARROW_X + 12, ay - 3, ARROW_X + 16, ay + 5, 0xFF606060);
        guiGraphics.fill(ARROW_X + 13, ay - 2, ARROW_X + 16, ay + 4, 0xFF606060);
        guiGraphics.fill(ARROW_X + 14, ay - 1, ARROW_X + 16, ay + 3, 0xFF606060);
    }

    @Override
    public ResourceLocation getRegistryName(BloodTankUpgradeJEIRecipe recipe) {
        return NVJeiRecipeIds.get(recipe);
    }
}
