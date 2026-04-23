package com.breakinblocks.neovitae.compat.jei.forge;

import com.google.common.collect.Lists;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeTransformRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class ForgeUpgradeRecipeCategory implements IRecipeCategory<ForgeRecipe> {

    public static final IRecipeType<ForgeRecipe> RECIPE_TYPE = IRecipeType.create(NeoVitae.MODID, "hellfire_forge_upgrade", ForgeRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final int WIDTH = 100;
    private static final int HEIGHT = 58;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public ForgeUpgradeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/hellfire_forge.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public IRecipeType<ForgeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.hellfire_forge_upgrade");
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
    public void getTooltip(ITooltipBuilder tooltip, ForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 40 && mouseX <= 60 && mouseY >= 21 && mouseY <= 34) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.minimumsouls", DECIMAL_FORMAT.format(recipe.getMinWill())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.soulsdrained", DECIMAL_FORMAT.format(recipe.getDrain())));
        }
    }

    @Override
    public void draw(ForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        var font = Minecraft.getInstance().font;
        var poseStack = guiGraphics.pose();

        poseStack.pushMatrix();
        poseStack.translate(40, 33);
        poseStack.scale(0.5f, 0.5f);
        guiGraphics.text(font, Component.translatable("jei.neovitae.recipe.will"), 0, 0, 0xFF8B8B8B);
        poseStack.popMatrix();

        if (recipe instanceof ForgeUpgradeRecipe) {
            poseStack.pushMatrix();
            poseStack.scale(0.5f, 0.5f);
            guiGraphics.textWithWordWrap(font, Component.translatable("jei.neovitae.recipe.upgrade_hint"), 0, 84, WIDTH * 2, 0xFFAA0000);
            poseStack.popMatrix();
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> validGems = Lists.newArrayList();
        for (HellfireForgeRecipeCategory.DefaultWill will : HellfireForgeRecipeCategory.DefaultWill.values()) {
            if (will.minSouls >= recipe.getMinWill()) {
                validGems.add(will.willStack);
            }
        }
        IRecipeSlotBuilder gems = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 43, 1);
        gems.addItemStacks(validGems);

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 14);
        if (recipe instanceof ForgeUpgradeRecipe) {
            ItemStack displayOutput = new ItemStack(Items.DIAMOND_SWORD);
            displayOutput.set(NVDataComponents.BLOOD_MENDING.get(), true);
            output.add(displayOutput);
        } else {
            output.add(recipe.getOutput());
        }

        List<? extends Ingredient> inputs = recipe.getCraftingIngredients();
        for (int index = 0; index < inputs.size(); index++) {
            int x = index % 2;
            int y = index / 2;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.add(inputs.get(index));
        }
    }
}
