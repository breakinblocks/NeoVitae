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
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FlaskRecipeCategory implements IRecipeCategory<FlaskRecipe> {
    public static final RecipeType<FlaskRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "flask", FlaskRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final int WIDTH = 118;
    private static final int HEIGHT = 40;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public FlaskRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVItems.ALCHEMY_FLASK.get()));
        background = guiHelper.createDrawable(NeoVitae.rl("gui/jei/alchemytable.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FlaskRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 58 && mouseX <= 78 && mouseY >= 21 && mouseY <= 34) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(recipe.getMinimumTier())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(recipe.getSyphon())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(recipe.getTicks())));
        }
    }

    @Override
    public void draw(FlaskRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.flask");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FlaskRecipe recipe, IFocusGroup focuses) {
        ItemStack outputStack = recipe.getOutput(recipe.getExampleFlask(), recipe.getExampleEffects());

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 14);
        output.addItemStack(outputStack);

        IRecipeSlotBuilder orb = builder.addSlot(RecipeIngredientRole.CATALYST, 61, 1);
        orb.addItemStacks(getOrbsForTier(recipe.getMinimumTier()));

        for (int index = 0; index < recipe.getInput().size(); index++) {
            int x = index % 3;
            int y = index / 3;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.addIngredients(recipe.getInput().get(index));
        }

        int flaskSlot = recipe.getInput().size();
        if (flaskSlot < 6) {
            int x = flaskSlot % 3;
            int y = flaskSlot / 3;
            IRecipeSlotBuilder flaskInput = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            flaskInput.addItemStack(recipe.getExampleFlask());
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
    public RecipeType<FlaskRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
