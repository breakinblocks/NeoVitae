package com.breakinblocks.neovitae.compat.jei.ritual;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.ritual.EnumRuneType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.compat.jei.NVJeiRecipeIds;

/**
 * JEI category for displaying ritual information.
 * Shows the ritual name, rune requirements, and EV costs
 * in a two-column layout.
 */
public class RitualRecipeCategory implements IRecipeCategory<RitualJEIRecipe> {

    public static final IRecipeType<RitualJEIRecipe> RECIPE_TYPE =
            IRecipeType.create(NeoVitae.MODID, "ritual", RitualJEIRecipe.class);

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
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.MASTER_RITUAL_STONE.block().get()));
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
    }

    @Override
    public IRecipeType<RitualJEIRecipe> getRecipeType() {
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
    public void draw(RitualJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        Component ritualName = recipe.getRitualName();
        int nameWidth = mc.font.width(ritualName);
        guiGraphics.text(mc.font, ritualName, (WIDTH - nameWidth) / 2, 2, 0xFF404040, false);

        int leftY = 16;

        Component crystalTier = recipe.getCrystalTierName();
        guiGraphics.text(mc.font, crystalTier, LEFT_COL, leftY, 0xFF606060, false);
        leftY += 12;

        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.recipe.ritual.activation"), LEFT_COL, leftY, 0xFF606060, false);
        leftY += 10;
        guiGraphics.text(mc.font, DECIMAL_FORMAT.format(recipe.activationCost()) + " EV", LEFT_COL + 4, leftY, 0xFF808080, false);
        leftY += 12;

        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.recipe.ritual.refresh"), LEFT_COL, leftY, 0xFF606060, false);
        leftY += 10;
        guiGraphics.text(mc.font, DECIMAL_FORMAT.format(recipe.refreshCost()) + " EV/op", LEFT_COL + 4, leftY, 0xFF808080, false);

        int rightY = 28;

        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.recipe.ritual.total_runes", recipe.getTotalRunes()), RIGHT_COL, rightY, 0xFF606060, false);
        rightY += 12;

        for (EnumRuneType runeType : EnumRuneType.values()) {
            int count = recipe.getRuneCount(runeType);
            if (count > 0) {
                String runeName = capitalize(runeType.getSerializedName());
                Component runeText = Component.translatable("jei.neovitae.ritual.rune_count", count, runeName)
                        .withStyle(runeType.colorCode);
                int runeColor = (runeType.colorCode.getColor() != null ? runeType.colorCode.getColor() : 0x808080) | 0xFF000000;
                guiGraphics.text(mc.font, runeText, RIGHT_COL + 4, rightY, runeColor, false);
                rightY += 10;
            }
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualJEIRecipe recipe, IFocusGroup focuses) {
    }

    @Override
    public Identifier getIdentifier(RitualJEIRecipe recipe) {
        return NVJeiRecipeIds.get(recipe);
    }
}
