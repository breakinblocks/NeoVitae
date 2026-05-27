package com.breakinblocks.neovitae.compat.jei.tabulavitae;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TabulaVitaeRecipeCategory implements IRecipeCategory<TabulaVitaeRecipe> {
    public static final IRecipeType<TabulaVitaeRecipe> RECIPE_TYPE = IRecipeType.create(NeoVitae.MODID, "alchemytable", TabulaVitaeRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final int WIDTH = 118;
    private static final int HEIGHT = 50;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public TabulaVitaeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.TABULA_VITAE.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/alchemytable.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, TabulaVitaeRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 53 && mouseX <= 93 && mouseY >= 43 && mouseY <= 49) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(recipe.getMinimumTier())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(recipe.getSyphon())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(recipe.getTicks())));
        }
    }

    @Override
    public void draw(TabulaVitaeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // Draw background
        background.draw(guiGraphics);

        var poseStack = guiGraphics.pose();

        poseStack.pushMatrix();
        poseStack.translate(67, 37);
        poseStack.scale(0.5f, 0.5f);
        guiGraphics.text(Minecraft.getInstance().font, Component.translatable("jei.neovitae.recipe.lp"), 0, 0, 0x8b8b8b);
        poseStack.popMatrix();

        poseStack.pushMatrix();
        poseStack.translate(53, 43);
        poseStack.scale(0.5f, 0.5f);
        guiGraphics.text(Minecraft.getInstance().font, Component.translatable("jei.neovitae.recipe.info"), 0, 0, 0x8b8b8b);
        poseStack.popMatrix();
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.tabulavitae");
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
    public void setRecipe(IRecipeLayoutBuilder builder, TabulaVitaeRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 14);
        output.add(recipe.getOutput());

        IRecipeSlotBuilder orb = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 61, 1);
        orb.addItemStacks(getOrbsForTier(recipe.getMinimumTier()));

        for (int index = 0; index < recipe.getInput().size(); index++) {
            int x = index % 3;
            int y = index / 3;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.add(recipe.getInput().get(index));
        }
    }

    private List<ItemStack> getOrbsForTier(int tier) {
        List<ItemStack> orbs = new ArrayList<>();
        if (tier <= 1) orbs.add(new ItemStack(NVItems.ORB_WEAK.get()));
        if (tier <= 2) orbs.add(new ItemStack(NVItems.ORB_APPRENTICE.get()));
        if (tier <= 3) orbs.add(new ItemStack(NVItems.ORB_MAGICIAN.get()));
        if (tier <= 4) orbs.add(new ItemStack(NVItems.ORB_MASTER.get()));
        if (tier <= 5) orbs.add(new ItemStack(NVItems.ORB_ARCHMAGE.get()));
        if (tier <= 6) orbs.add(new ItemStack(NVItems.ORB_TRANSCENDENT.get()));
        return orbs;
    }

    @Override
    public IRecipeType<TabulaVitaeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
