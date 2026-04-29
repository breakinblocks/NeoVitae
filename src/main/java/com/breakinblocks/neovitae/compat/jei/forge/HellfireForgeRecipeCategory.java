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
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class HellfireForgeRecipeCategory implements IRecipeCategory<ForgeRecipe> {

    public static final RecipeType<ForgeRecipe> RECIPE_TYPE = RecipeType.create(NeoVitae.MODID, "hellfire_forge", ForgeRecipe.class);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public HellfireForgeRecipeCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVBlocks.HELLFIRE_FORGE.block().get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/hellfire_forge.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Override
    public RecipeType<ForgeRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.hellfire_forge");
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
    public void draw(ForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(40, 33, 0);
        poseStack.scale(0.5f, 0.5f, 1f);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.neovitae.recipe.will"), 0, 0, 0x8b8b8b, false);
        poseStack.popPose();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgeRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> validGems = Lists.newArrayList();
        for (DefaultWill will : DefaultWill.values()) {
            if (will.minSouls >= recipe.getMinWill()) {
                validGems.add(will.spiritusStack);
            }
        }
        IRecipeSlotBuilder gems = builder.addSlot(RecipeIngredientRole.CATALYST, 43, 1);
        gems.addItemStacks(validGems);

        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 14);
        output.addItemStack(recipe.getOutput());

        List<? extends net.minecraft.world.item.crafting.Ingredient> inputs = recipe.getCraftingIngredients();
        for (int index = 0; index < inputs.size(); index++) {
            int x = index % 2;
            int y = index / 2;
            IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1);
            input.addIngredients(inputs.get(index));
        }
    }

    public enum DefaultWill {
        RAW(new ItemStack(NVItems.RAW_SPIRITUS.get()), 16),
        PETTY(new ItemStack(NVItems.SPIRITUS_GEM_PETTY.get()), 64),
        LESSER(new ItemStack(NVItems.SPIRITUS_GEM_LESSER.get()), 256),
        COMMON(new ItemStack(NVItems.SPIRITUS_GEM_COMMON.get()), 1024),
        GREATER(new ItemStack(NVItems.SPIRITUS_GEM_GREATER.get()), 4096),
        GRAND(new ItemStack(NVItems.SPIRITUS_GEM_GRAND.get()), 16384);

        public final ItemStack spiritusStack;
        public final double minSouls;

        DefaultWill(ItemStack spiritusStack, double minSouls) {
            this.spiritusStack = spiritusStack;
            this.minSouls = minSouls;
        }
    }
}
