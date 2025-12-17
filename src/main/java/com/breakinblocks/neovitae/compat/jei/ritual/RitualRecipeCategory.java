package com.breakinblocks.neovitae.compat.jei.ritual;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.ritual.EnumRuneType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;

/**
 * JEI category for displaying ritual information.
 * Shows the ritual name, rune requirements, and LP costs
 * in a two-column layout.
 */
public class RitualRecipeCategory implements IRecipeCategory<RitualJEIRecipe> {

    public static final RecipeType<RitualJEIRecipe> RECIPE_TYPE =
            RecipeType.create(NeoVitae.MODID, "ritual", RitualJEIRecipe.class);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");

    // Layout dimensions
    private static final int WIDTH = 160;
    private static final int HEIGHT = 85;

    // Column positions
    private static final int LEFT_COL = 4;
    private static final int RIGHT_COL = 85;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public RitualRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(BMBlocks.MASTER_RITUAL_STONE.block().get()));
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<RitualJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.ritual");
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
    public void draw(RitualJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        // Draw ritual name at the top (centered)
        Component ritualName = recipe.getRitualName();
        int nameWidth = mc.font.width(ritualName);
        guiGraphics.drawString(mc.font, ritualName, (WIDTH - nameWidth) / 2, 2, 0x404040, false);

        // LEFT COLUMN - LP costs and crystal requirement
        int leftY = 16;

        // Crystal tier requirement (shortened)
        Component crystalTier = recipe.getCrystalTierName();
        guiGraphics.drawString(mc.font, crystalTier, LEFT_COL, leftY, 0x606060, false);
        leftY += 12;

        // Activation cost
        guiGraphics.drawString(mc.font, Component.translatable("jei.neovitae.recipe.ritual.activation"), LEFT_COL, leftY, 0x606060, false);
        leftY += 10;
        guiGraphics.drawString(mc.font, DECIMAL_FORMAT.format(recipe.activationCost()) + " LP", LEFT_COL + 4, leftY, 0x808080, false);
        leftY += 12;

        // Refresh cost
        guiGraphics.drawString(mc.font, Component.translatable("jei.neovitae.recipe.ritual.refresh"), LEFT_COL, leftY, 0x606060, false);
        leftY += 10;
        guiGraphics.drawString(mc.font, DECIMAL_FORMAT.format(recipe.refreshCost()) + " LP/op", LEFT_COL + 4, leftY, 0x808080, false);

        // RIGHT COLUMN - Rune counts
        int rightY = 28;

        // Total runes header
        guiGraphics.drawString(mc.font, Component.translatable("jei.neovitae.recipe.ritual.total_runes", recipe.getTotalRunes()), RIGHT_COL, rightY, 0x606060, false);
        rightY += 12;

        // Rune breakdown with colors
        for (EnumRuneType runeType : EnumRuneType.values()) {
            int count = recipe.getRuneCount(runeType);
            if (count > 0) {
                String runeName = capitalize(runeType.getSerializedName());
                Component runeText = Component.literal(count + "x " + runeName)
                        .withStyle(runeType.colorCode);
                guiGraphics.drawString(mc.font, runeText, RIGHT_COL + 4, rightY, runeType.colorCode.getColor() != null ?
                        runeType.colorCode.getColor() : 0x808080, false);
                rightY += 10;
            }
        }
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualJEIRecipe recipe, IFocusGroup focuses) {
        // No slots needed - the master ritual stone is already shown as the category icon
        // and as the catalyst that brings users to this page
    }
}
