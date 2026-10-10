package com.breakinblocks.neovitae.compat.jei.dungeon;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DungeonStoneSpreadCategory implements IRecipeCategory<DungeonStoneSpreadJEIRecipe> {

    public static final IRecipeType<DungeonStoneSpreadJEIRecipe> RECIPE_TYPE =
            IRecipeType.create(NeoVitae.MODID, "dungeon_stone_spread", DungeonStoneSpreadJEIRecipe.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 100;
    private static final int CELL = 18;
    private static final int GAP = 6;

    private static final int FIELD_COLS = 7;
    private static final int FIELD_ROWS = 3;
    private static final int FIELD_Y = 44;
    private static final int SEED_COL = FIELD_COLS / 2;
    private static final int SEED_ROW = FIELD_ROWS / 2;

    private static final long STEP_MS = 650;
    private static final long FLASH_MS = 450;
    private static final long HOLD_MS = 1600;
    private static final int MAX_DISTANCE = SEED_COL + SEED_ROW;
    private static final long CYCLE_MS = (MAX_DISTANCE + 1) * STEP_MS + HOLD_MS;

    private final IDrawable icon;
    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final ItemStack stone = new ItemStack(Items.STONE);

    private final int gridX;
    private final int arrowX;
    private final int arrowY;
    private final int outputX;
    private final int outputY;
    private final int fieldX;

    public DungeonStoneSpreadCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(DungeonBlocks.DUNGEON_STONE.get(DungeonVariant.RAW).item().get()));
        slot = guiHelper.getSlotDrawable();
        arrow = guiHelper.getRecipeArrow();

        int rowWidth = CELL * 2 + GAP + arrow.getWidth() + GAP + CELL;
        gridX = (WIDTH - rowWidth) / 2;
        arrowX = gridX + CELL * 2 + GAP;
        arrowY = CELL - arrow.getHeight() / 2;
        outputX = arrowX + arrow.getWidth() + GAP;
        outputY = CELL / 2;
        fieldX = (WIDTH - FIELD_COLS * CELL) / 2;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.dungeon_stone_spread");
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
    public IRecipeType<DungeonStoneSpreadJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull DungeonStoneSpreadJEIRecipe recipe,
            @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, gridX + 1, 1).add(recipe.dungeonStone());
        builder.addSlot(RecipeIngredientRole.INPUT, gridX + CELL + 1, 1).add(stone);
        builder.addSlot(RecipeIngredientRole.INPUT, gridX + 1, CELL + 1).add(stone);
        builder.addSlot(RecipeIngredientRole.INPUT, gridX + CELL + 1, CELL + 1).add(stone);

        builder.addSlot(RecipeIngredientRole.OUTPUT, outputX + 1, outputY + 1)
                .add(recipe.dungeonStone().copyWithCount(4));
    }

    @Override
    public void draw(DungeonStoneSpreadJEIRecipe recipe, IRecipeSlotsView slotsView, GuiGraphicsExtractor guiGraphics,
            double mouseX, double mouseY) {
        slot.draw(guiGraphics, gridX, 0);
        slot.draw(guiGraphics, gridX + CELL, 0);
        slot.draw(guiGraphics, gridX, CELL);
        slot.draw(guiGraphics, gridX + CELL, CELL);
        arrow.draw(guiGraphics, arrowX, arrowY);
        slot.draw(guiGraphics, outputX, outputY);

        int fieldWidth = FIELD_COLS * CELL;
        int fieldHeight = FIELD_ROWS * CELL;
        guiGraphics.fill(fieldX - 2, FIELD_Y - 2, fieldX + fieldWidth + 2, FIELD_Y + fieldHeight + 2, 0xFF373737);
        guiGraphics.fill(fieldX - 1, FIELD_Y - 1, fieldX + fieldWidth + 1, FIELD_Y + fieldHeight + 1, 0xFF8B8B8B);

        int aspectColor = SpiritusTooltipHelper.spiritusColor(recipe.variant().getSpiritusType()) & 0xFFFFFF;
        long elapsed = Util.getMillis() % CYCLE_MS;

        for (int row = 0; row < FIELD_ROWS; row++) {
            for (int col = 0; col < FIELD_COLS; col++) {
                int x = fieldX + col * CELL;
                int y = FIELD_Y + row * CELL;
                int distance = Math.abs(col - SEED_COL) + Math.abs(row - SEED_ROW);
                long sinceConverted = elapsed - distance * STEP_MS;
                boolean converted = distance == 0 || sinceConverted >= 0;

                if (converted && sinceConverted < FLASH_MS && distance > 0) {
                    int alpha = (int) (0xC0 * (1.0 - (double) sinceConverted / FLASH_MS));
                    guiGraphics.fill(x, y, x + CELL, y + CELL, (alpha << 24) | aspectColor);
                }

                guiGraphics.item(converted ? recipe.dungeonStone() : stone, x + 1, y + 1);
            }
        }
    }

    @Override
    public Identifier getIdentifier(DungeonStoneSpreadJEIRecipe recipe) {
        return NeoVitae.rl("dungeon_stone_spread/" + recipe.variant().getName());
    }
}
