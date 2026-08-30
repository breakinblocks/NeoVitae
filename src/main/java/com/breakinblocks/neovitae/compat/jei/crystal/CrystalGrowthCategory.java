package com.breakinblocks.neovitae.compat.jei.crystal;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;

public class CrystalGrowthCategory implements IRecipeCategory<CrystalGrowthJEIRecipe> {

    public static final RecipeType<CrystalGrowthJEIRecipe> RECIPE_TYPE =
            RecipeType.create(NeoVitae.MODID, "crystal_growth", CrystalGrowthJEIRecipe.class);

    private static final int WIDTH = 162;
    private static final int HEIGHT = 94;

    private static final int ROW_Y = 4;
    private static final int GAP = 3;
    private static final int TEXT_X = 3;
    private static final int TEXT_TOP = 28;
    private static final int LINE = 10;

    private static final NumberFormat FORMAT = NumberFormat.getIntegerInstance();

    private final IDrawable icon;
    private final IDrawableStatic slot;
    private final IDrawableStatic arrowBackground;
    private final IDrawableAnimated formArrow;
    private final IDrawableAnimated growArrow;

    private final int slot1X;
    private final int slot2X;
    private final int slot3X;
    private final int arrow1X;
    private final int arrow2X;
    private final int arrowY;

    public CrystalGrowthCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.CRYSTALLARIUM_MALEFICUM.block().get()));
        slot = guiHelper.getSlotDrawable();
        arrowBackground = guiHelper.getRecipeArrow();
        formArrow = guiHelper.createAnimatedRecipeArrow(100);
        growArrow = guiHelper.createAnimatedRecipeArrow(160);

        int slotW = slot.getWidth();
        int arrowW = arrowBackground.getWidth();
        int rowWidth = slotW * 3 + arrowW * 2 + GAP * 4;

        slot1X = Math.max(TEXT_X, (WIDTH - rowWidth) / 2);
        arrow1X = slot1X + slotW + GAP;
        slot2X = arrow1X + arrowW + GAP;
        arrow2X = slot2X + slotW + GAP;
        slot3X = arrow2X + arrowW + GAP;
        arrowY = ROW_Y + (slotW - arrowBackground.getHeight()) / 2;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.crystal_growth");
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
    public RecipeType<CrystalGrowthJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CrystalGrowthJEIRecipe recipe,
            @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, slot1X + 1, ROW_Y + 1)
                .addItemStack(new ItemStack(NVBlocks.CRYSTALLARIUM_MALEFICUM.block().get()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, slot2X + 1, ROW_Y + 1)
                .addItemStack(recipe.cluster());

        builder.addSlot(RecipeIngredientRole.OUTPUT, slot3X + 1, ROW_Y + 1)
                .addItemStack(recipe.shard())
                .addRichTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("jei.neovitae.crystal_growth.harvest_hand",
                            FORMAT.format(recipe.harvestSpiritus())).withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.translatable("jei.neovitae.crystal_growth.harvest_regrow")
                            .withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.translatable("jei.neovitae.crystal_growth.harvest_ritual")
                            .withStyle(ChatFormatting.GRAY));
                    tooltip.add(Component.translatable("jei.neovitae.crystal_growth.harvest_mining")
                            .withStyle(ChatFormatting.DARK_GRAY));
                });
    }

    @Override
    public void draw(CrystalGrowthJEIRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        slot.draw(guiGraphics, slot1X, ROW_Y);
        slot.draw(guiGraphics, slot2X, ROW_Y);
        slot.draw(guiGraphics, slot3X, ROW_Y);

        arrowBackground.draw(guiGraphics, arrow1X, arrowY);
        formArrow.draw(guiGraphics, arrow1X, arrowY);
        arrowBackground.draw(guiGraphics, arrow2X, arrowY);
        growArrow.draw(guiGraphics, arrow2X, arrowY);

        var font = Minecraft.getInstance().font;
        int color = SpiritusTooltipHelper.spiritusColor(recipe.type());

        line(guiGraphics, font, 0, Component.translatable("jei.neovitae.crystal_growth.aspect",
                Component.translatable("tooltip.neovitae.spiritus." + recipe.type().getSerializedName())
                        .withColor(color)));

        line(guiGraphics, font, 1, Component.translatable("jei.neovitae.crystal_growth.seed",
                FORMAT.format(recipe.spiritusToForm()), FORMAT.format(recipe.formationTicks() / 20))
                .withStyle(ChatFormatting.DARK_GRAY));

        line(guiGraphics, font, 2, Component.translatable("jei.neovitae.crystal_growth.segment",
                FORMAT.format(recipe.spiritusPerSegment()), recipe.maxSegments())
                .withStyle(ChatFormatting.DARK_GRAY));

        line(guiGraphics, font, 3, Component.translatable("jei.neovitae.crystal_growth.grow")
                .withStyle(ChatFormatting.DARK_GRAY));

        line(guiGraphics, font, 4, Component.translatable("jei.neovitae.crystal_growth.harvest",
                FORMAT.format(recipe.harvestSpiritus()))
                .withStyle(ChatFormatting.DARK_GRAY));

        line(guiGraphics, font, 5, Component.translatable("jei.neovitae.crystal_growth.air")
                .withStyle(ChatFormatting.DARK_RED));
    }

    private void line(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, int index, Component text) {
        int y = TEXT_TOP + index * LINE;
        int available = WIDTH - TEXT_X * 2;
        int textWidth = font.width(text);
        if (textWidth <= available) {
            guiGraphics.drawString(font, text, TEXT_X, y, 0xFF404040, false);
            return;
        }

        float scale = available / (float) textWidth;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(TEXT_X, y + (1 - scale) * font.lineHeight / 2f, 0);
        guiGraphics.pose().scale(scale, scale, 1f);
        guiGraphics.drawString(font, text, 0, 0, 0xFF404040, false);
        guiGraphics.pose().popPose();
    }
}
