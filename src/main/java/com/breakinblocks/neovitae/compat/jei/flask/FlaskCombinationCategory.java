package com.breakinblocks.neovitae.compat.jei.flask;

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
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.compat.jei.NVJeiRecipeIds;

public class FlaskCombinationCategory implements IRecipeCategory<FlaskCombinationJEIRecipe> {
    public static final RecipeType<FlaskCombinationJEIRecipe> RECIPE_TYPE =
            RecipeType.create(NeoVitae.MODID, "flask_combination", FlaskCombinationJEIRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final int WIDTH = 118;
    private static final int HEIGHT = 40;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public FlaskCombinationCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVItems.ALCHEMY_FLASK.get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/alchemytable.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FlaskCombinationJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 58 && mouseX <= 78 && mouseY >= 21 && mouseY <= 34) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(recipe.minimumTier())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(recipe.syphon())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(recipe.ticks())));
        }
    }

    @Override
    public void draw(FlaskCombinationJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.flask_combination");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FlaskCombinationJEIRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 14);
        output.addItemStack(recipe.outputFlask());

        IRecipeSlotBuilder orb = builder.addSlot(RecipeIngredientRole.CATALYST, 61, 1);
        orb.addItemStacks(getOrbsForTier(recipe.minimumTier()));

        IRecipeSlotBuilder flaskInput = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
        flaskInput.addItemStack(recipe.inputFlask());

        for (int index = 0; index < recipe.ingredients().size(); index++) {
            int slot = index + 1;
            int x = slot % 3;
            int y = slot / 3;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.addIngredients(recipe.ingredients().get(index));
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
    public RecipeType<FlaskCombinationJEIRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public ResourceLocation getRegistryName(FlaskCombinationJEIRecipe recipe) {
        return NVJeiRecipeIds.get(recipe);
    }
}
