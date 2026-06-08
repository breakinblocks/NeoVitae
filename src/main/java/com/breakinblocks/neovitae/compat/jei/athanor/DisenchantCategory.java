package com.breakinblocks.neovitae.compat.jei.athanor;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DisenchantCategory implements IRecipeCategory<DisenchantJEIRecipe> {
    public static final IRecipeType<DisenchantJEIRecipe> RECIPE_TYPE =
            IRecipeType.create(NeoVitae.MODID, "disenchant", DisenchantJEIRecipe.class);

    private static final int WIDTH = 120;
    private static final int HEIGHT = 64;
    private static final int ROW = 12;
    private static final int GREEN = 0x2E8B57;
    private static final int RAW_COLOR = 0xFFAA3333;

    @Nonnull private final IDrawable icon;
    @Nonnull private final IDrawable slot;

    public DisenchantCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVItems.SANGUINE_REVERTER.get()));
        slot = guiHelper.getSlotDrawable();
    }

    @Nonnull @Override public Component getTitle() { return Component.translatable("jei.neovitae.recipe.disenchant"); }
    @Override public int getWidth() { return WIDTH; }
    @Override public int getHeight() { return HEIGHT; }
    @Nullable @Override public IDrawable getIcon() { return icon; }
    @Override public IRecipeType<DisenchantJEIRecipe> getRecipeType() { return RECIPE_TYPE; }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull DisenchantJEIRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, ROW + 1).addItemStacks(recipe.enchantedBooks());
        builder.addSlot(RecipeIngredientRole.INPUT, 20, ROW + 1).add(new ItemStack(Items.BOOK));

        IRecipeSlotBuilder fluid = builder.addSlot(RecipeIngredientRole.INPUT, 38, ROW + 1);
        fluid.add(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 100);
        fluid.addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.literal("100 mB")));

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 59, ROW + 1);
        catalyst.add(new ItemStack(NVItems.SANGUINE_REVERTER.get()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, ROW + 1).addItemStacks(recipe.enchantedBooks());
    }

    @Override
    public void draw(DisenchantJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.disenchant.any_item"), 2, 2, GREEN);

        slot.draw(guiGraphics, 1, ROW);
        slot.draw(guiGraphics, 19, ROW);
        slot.draw(guiGraphics, 37, ROW);
        slot.draw(guiGraphics, 58, ROW);
        slot.draw(guiGraphics, 99, ROW);

        int ay = ROW + 6;
        guiGraphics.fill(79, ay, 95, ay + 2, 0xFF606060);
        guiGraphics.fill(91, ay - 3, 95, ay + 5, 0xFF606060);
        guiGraphics.fill(92, ay - 2, 95, ay + 4, 0xFF606060);
        guiGraphics.fill(93, ay - 1, 95, ay + 3, 0xFF606060);

        int cy = ROW + 22;
        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.disenchant.per_enchant"), 2, cy, 0xAAAAAA);
        guiGraphics.fill(2, cy + 11, 6, cy + 15, RAW_COLOR);
        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.disenchant.spiritus"), 8, cy + 10, 0xFFFFFF);
        guiGraphics.text(mc.font, Component.translatable("jei.neovitae.disenchant.ev"), 2, cy + 20, 0xFFFFFF);
    }
}
