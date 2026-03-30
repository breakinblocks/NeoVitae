package com.breakinblocks.neovitae.compat.jei.imperfectritual;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * JEI category for displaying imperfect ritual recipes.
 * Shows the ritual stone with the required catalyst block above it,
 * along with EV cost and effect description.
 */
public class ImperfectRitualRecipeCategory implements IRecipeCategory<ImperfectRitualJEIRecipe> {

    public static final RecipeType<ImperfectRitualJEIRecipe> RECIPE_TYPE =
            RecipeType.create(NeoVitae.MODID, "imperfect_ritual", ImperfectRitualJEIRecipe.class);

    private static final int WIDTH = 160;
    private static final int HEIGHT = 80;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;

    public ImperfectRitualRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()));
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<ImperfectRitualJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.imperfect_ritual");
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
    public void draw(ImperfectRitualJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        Component ritualName = recipe.getRitualName();
        int nameWidth = mc.font.width(ritualName);
        guiGraphics.drawString(mc.font, ritualName, (WIDTH - nameWidth) / 2, 2, Color.DARK_GRAY.getRGB(), false);

        String lpCost = recipe.activationCost() + " EV";
        if (recipe.consumesBlock()) {
            lpCost += " (Consumed)";
        }
        int lpWidth = mc.font.width(lpCost);
        int lpColor = recipe.consumesBlock() ? 0xCC4444 : Color.GRAY.getRGB();
        guiGraphics.drawString(mc.font, lpCost, (WIDTH - lpWidth) / 2, 53, lpColor, false);

        Component desc = recipe.description();
        int descY = 65;
        int descWidth = mc.font.width(desc);
        if (descWidth <= WIDTH - 4) {
            guiGraphics.drawString(mc.font, desc, (WIDTH - descWidth) / 2, descY, Color.DARK_GRAY.getRGB(), false);
        } else {
            guiGraphics.drawString(mc.font, desc, 2, descY, Color.DARK_GRAY.getRGB(), false);
        }

        slotDrawable.draw(guiGraphics, 71, 15);
        slotDrawable.draw(guiGraphics, 71, 33);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ImperfectRitualJEIRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder catalystSlot = builder.addSlot(RecipeIngredientRole.INPUT, 72, 16);
        catalystSlot.addItemStacks(recipe.catalystBlock());

        IRecipeSlotBuilder ritualStoneSlot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 72, 34);
        ritualStoneSlot.addItemStack(new ItemStack(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()));
    }
}
