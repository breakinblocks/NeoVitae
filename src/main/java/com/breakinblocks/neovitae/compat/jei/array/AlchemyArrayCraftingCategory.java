package com.breakinblocks.neovitae.compat.jei.array;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchemyArrayCraftingCategory implements IRecipeCategory<AlchemyArrayRecipe> {
    public static final RecipeType<AlchemyArrayRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "alchemyarray", AlchemyArrayRecipe.class);

    private static final int WIDTH = 100;
    private static final int HEIGHT = 30;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public AlchemyArrayCraftingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(BMItems.ARCANE_ASHES.get()));
        background = guiHelper.createDrawable(NeoVitae.rl("gui/jei/binding.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.alchemyarraycrafting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyArrayRecipe recipe, IFocusGroup focuses) {
        // Only add output slot if there's an actual item output
        if (!recipe.getOutput().isEmpty()) {
            IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 6);
            output.addItemStack(recipe.getOutput());
        }
        // For effect-only recipes, we'll draw the array texture in the draw() method

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.INPUT, 30, 4);
        catalyst.addIngredients(recipe.getAddedInput());

        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 1, 6);
        input.addIngredients(recipe.getBaseInput());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AlchemyArrayRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // Show effect description when hovering over output area for effect recipes
        if (recipe.getOutput().isEmpty() && mouseX >= 70 && mouseX <= 95 && mouseY >= 2 && mouseY <= 27) {
            AlchemyArrayEffectType effectType = recipe.getEffectType();
            tooltip.add(Component.translatable("jei.neovitae.effect." + effectType.getSerializedName() + ".name"));
            tooltip.add(Component.translatable("jei.neovitae.effect." + effectType.getSerializedName() + ".desc"));
        }
    }

    @Override
    public void draw(AlchemyArrayRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw background
        background.draw(guiGraphics);

        // For effect-only recipes, draw the alchemy array texture as the output
        if (recipe.getOutput().isEmpty()) {
            ResourceLocation textureRL = recipe.getTexture();
            // Recipe already stores full path like "neovitae:textures/models/alchemyarrays/bouncearray.png"
            // Draw the array texture in the output slot area (16x16 at position 74, 6)
            RenderSystem.setShaderTexture(0, textureRL);
            guiGraphics.blit(textureRL, 74, 6, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public RecipeType<AlchemyArrayRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
