package com.breakinblocks.neovitae.compat.jei.alchemytable;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AlchemyTableRecipeCategory implements IRecipeCategory<AlchemyTableRecipe> {
    public static final RecipeType<AlchemyTableRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "alchemytable", AlchemyTableRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final int WIDTH = 118;
    private static final int HEIGHT = 40;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public AlchemyTableRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(BMBlocks.ALCHEMY_TABLE.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("gui/jei/alchemytable.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AlchemyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 58 && mouseX <= 78 && mouseY >= 21 && mouseY <= 34) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(recipe.getMinimumTier() + 1)));
            tooltip.add(Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(recipe.getSyphon())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(recipe.getTicks())));
        }
    }

    @Override
    public void draw(AlchemyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw background
        background.draw(guiGraphics);

        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(64, 23, 0);
        poseStack.scale(0.5f, 0.5f, 1f);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.neovitae.recipe.lp"), 0, 0, 0x8b8b8b, false);
        poseStack.translate(-8, 15, 0);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.neovitae.recipe.info"), 0, 0, 0x8b8b8b, false);
        poseStack.popPose();
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.alchemytable");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyTableRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 14);
        output.addItemStack(recipe.getOutput());

        // Add orbs that meet the tier requirement
        IRecipeSlotBuilder orb = builder.addSlot(RecipeIngredientRole.CATALYST, 61, 1);
        orb.addItemStacks(getOrbsForTier(recipe.getMinimumTier()));

        for (int index = 0; index < recipe.getInput().size(); index++) {
            int x = index % 3;
            int y = index / 3;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.addIngredients(recipe.getInput().get(index));
        }
    }

    private List<ItemStack> getOrbsForTier(int tier) {
        List<ItemStack> orbs = new ArrayList<>();
        // Add all orbs at or above the required tier
        if (tier <= 1) orbs.add(new ItemStack(BMItems.ORB_WEAK.get()));
        if (tier <= 2) orbs.add(new ItemStack(BMItems.ORB_APPRENTICE.get()));
        if (tier <= 3) orbs.add(new ItemStack(BMItems.ORB_MAGICIAN.get()));
        if (tier <= 4) orbs.add(new ItemStack(BMItems.ORB_MASTER.get()));
        if (tier <= 5) orbs.add(new ItemStack(BMItems.ORB_ARCHMAGE.get()));
        if (tier <= 6) orbs.add(new ItemStack(BMItems.ORB_TRANSCENDENT.get()));
        return orbs;
    }

    @Override
    public RecipeType<AlchemyTableRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
