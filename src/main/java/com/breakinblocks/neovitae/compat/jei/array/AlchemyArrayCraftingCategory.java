package com.breakinblocks.neovitae.compat.jei.array;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * JEI category for alchemy array recipes that produce an item output
 * ({@link AlchemyArrayEffectType#CRAFTING CRAFTING}
 * and {@link AlchemyArrayEffectType#BINDING BINDING}
 * effect types). Persistent environmental effects live in the sibling
 * {@link AlchemyArrayEffectCategory}.
 */
public class AlchemyArrayCraftingCategory implements IRecipeCategory<AlchemyArrayRecipe> {
    public static final IRecipeType<AlchemyArrayRecipe> RECIPE_TYPE =
            IRecipeType.create(NeoVitae.MODID, "array_crafting", AlchemyArrayRecipe.class);

    private static final int WIDTH = 100;
    private static final int HEIGHT = 30;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public AlchemyArrayCraftingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVItems.ARCANE_SCRIBE_TOOL.get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/binding.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.array_crafting");
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
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 6);
        if (!recipe.getOutput().isEmpty()) {
            output.add(recipe.getOutput());
        }

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.INPUT, 30, 4);
        catalyst.add(recipe.getAddedInput());

        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 1, 6);
        input.add(recipe.getBaseInput());
    }

    @Override
    public void draw(AlchemyArrayRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView,
                     GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Override
    public IRecipeType<AlchemyArrayRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
