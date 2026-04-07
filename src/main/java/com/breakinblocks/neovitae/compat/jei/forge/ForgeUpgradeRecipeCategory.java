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
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeUpgradeRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class ForgeUpgradeRecipeCategory implements IRecipeCategory<ForgeUpgradeRecipe> {

    public static final RecipeType<ForgeUpgradeRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "hellfire_forge_upgrade", ForgeUpgradeRecipe.class);
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
    public RecipeType<ForgeUpgradeRecipe> getRecipeType() {
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
    public void getTooltip(ITooltipBuilder tooltip, ForgeUpgradeRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 40 && mouseX <= 60 && mouseY >= 21 && mouseY <= 34) {
            tooltip.add(Component.translatable("jei.neovitae.recipe.minimumsouls", DECIMAL_FORMAT.format(recipe.getMinWill())));
            tooltip.add(Component.translatable("jei.neovitae.recipe.soulsdrained", DECIMAL_FORMAT.format(recipe.getDrain())));
        }
    }

    @Override
    public void draw(ForgeUpgradeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        var font = Minecraft.getInstance().font;
        var poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(40, 33, 0);
        poseStack.scale(0.5f, 0.5f, 1f);
        guiGraphics.drawString(font, Component.translatable("jei.neovitae.recipe.will"), 0, 0, 0x8b8b8b, false);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 1f);
        guiGraphics.drawWordWrap(font, Component.translatable("jei.neovitae.recipe.upgrade_hint"), 0, 84, WIDTH * 2, 0xAA0000);
        poseStack.popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeUpgradeRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> validGems = Lists.newArrayList();
        for (HellfireForgeRecipeCategory.DefaultWill will : HellfireForgeRecipeCategory.DefaultWill.values()) {
            if (will.minSouls >= recipe.getMinWill()) {
                validGems.add(will.willStack);
            }
        }
        IRecipeSlotBuilder gems = builder.addSlot(RecipeIngredientRole.CATALYST, 43, 1);
        gems.addItemStacks(validGems);

        ItemStack displayOutput = new ItemStack(Items.DIAMOND_SWORD);
        displayOutput.set(NVDataComponents.BLOOD_MENDING.get(), true);
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 14);
        output.addItemStack(displayOutput);

        IRecipeSlotBuilder equipSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 1);
        equipSlot.addItemStack(new ItemStack(Items.DIAMOND_SWORD));

        List<? extends Ingredient> catalysts = recipe.getCraftingIngredients();
        for (int index = 0; index < catalysts.size(); index++) {
            int gridIndex = index + 1;
            int x = gridIndex % 2;
            int y = gridIndex / 2;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.addIngredients(catalysts.get(index));
        }
    }
}
