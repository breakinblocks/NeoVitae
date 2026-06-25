package com.breakinblocks.neovitae.compat.jei.array;

import net.minecraft.client.renderer.RenderPipelines;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * JEI category for alchemy array recipes that place a persistent environmental
 * effect (e.g. Bounce, Spike, Furnace, Undertow, Endless Fountain). The array
 * texture is drawn in the output slot and a descriptive tooltip is attached.
 * Item-producing arrays (CRAFTING / BINDING effect types) are handled by the
 * sibling {@link AlchemyArrayCraftingCategory}.
 */
public class AlchemyArrayEffectCategory implements IRecipeCategory<AlchemyArrayRecipe> {
    public static final IRecipeType<AlchemyArrayRecipe> RECIPE_TYPE =
            IRecipeType.create(NeoVitae.MODID, "array_effects", AlchemyArrayRecipe.class);

    private static final int WIDTH = 100;
    private static final int HEIGHT = 30;

    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;

    public AlchemyArrayEffectCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(new ItemStack(NVItems.ARCANE_SCRIBE_TOOL.get()));
        background = guiHelper.createDrawable(NeoVitae.rl("textures/gui/jei/binding.png"), 0, 0, WIDTH, HEIGHT);
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.neovitae.recipe.array_effects");
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

    private static ItemStack getEffectDummyItem(AlchemyArrayEffectType type) {
        return switch (type) {
            case BOUNCE -> new ItemStack(NVItems.ARRAY_BOUNCE.get());
            case SPIKE -> new ItemStack(NVItems.ARRAY_SPIKE.get());
            case UPDRAFT -> new ItemStack(NVItems.ARRAY_UPDRAFT.get());
            case MOVEMENT -> new ItemStack(NVItems.ARRAY_MOVEMENT.get());
            case DAY -> new ItemStack(NVItems.ARRAY_DAY.get());
            case NIGHT -> new ItemStack(NVItems.ARRAY_NIGHT.get());
            case ELEVATOR -> new ItemStack(NVItems.ARRAY_ELEVATOR.get());
            case REPULSION -> new ItemStack(NVItems.ARRAY_REPULSION.get());
            case COLLECTION -> new ItemStack(NVItems.ARRAY_COLLECTION.get());
            case LIGHT -> new ItemStack(NVItems.ARRAY_LIGHT.get());
            case FURNACE -> new ItemStack(NVItems.ARRAY_FURNACE.get());
            case RAIN -> new ItemStack(NVItems.ARRAY_RAIN.get());
            case GROWTH -> new ItemStack(NVItems.ARRAY_GROWTH.get());
            case FREEZE -> new ItemStack(NVItems.ARRAY_FREEZE.get());
            case SIGNAL -> new ItemStack(NVItems.ARRAY_SIGNAL.get());
            case TRIGGER -> new ItemStack(NVItems.ARRAY_TRIGGER.get());
            case SPIRIT_SIPHON -> new ItemStack(NVItems.ARRAY_SPIRIT_SIPHON.get());
            case DEFLECTION -> new ItemStack(NVItems.ARRAY_DEFLECTION.get());
            case ENDLESS_FOUNTAIN -> new ItemStack(NVItems.ARRAY_ENDLESS_FOUNTAIN.get());
            case UNDERTOW -> new ItemStack(NVItems.ARRAY_UNDERTOW.get());
            case LOYAL_FRIENDS -> new ItemStack(NVItems.ARRAY_LOYAL_FRIENDS.get());
            case VORTEX -> new ItemStack(NVItems.ARRAY_VORTEX.get());
            case IMPRISONMENT -> new ItemStack(NVItems.ARRAY_IMPRISONMENT.get());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyArrayRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 74, 6);
        ItemStack dummy = getEffectDummyItem(recipe.getEffectType());
        if (!dummy.isEmpty()) {
            output.add(dummy);
        }

        IRecipeSlotBuilder catalyst = builder.addSlot(RecipeIngredientRole.INPUT, 30, 4);
        catalyst.add(recipe.getAddedInput());

        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 1, 6);
        input.add(recipe.getBaseInput());
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AlchemyArrayRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 70 && mouseX <= 95 && mouseY >= 2 && mouseY <= 27) {
            AlchemyArrayEffectType effectType = recipe.getEffectType();
            tooltip.add(Component.translatable("jei.neovitae.effect." + effectType.getSerializedName() + ".name"));
            tooltip.add(Component.translatable("jei.neovitae.effect." + effectType.getSerializedName() + ".desc"));
        }
    }

    @Override
    public void draw(AlchemyArrayRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);

        Identifier textureRL = recipe.getTexture();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, textureRL, 74, 6, 0f, 0f, 16, 16, 16, 16);
    }

    @Override
    public IRecipeType<AlchemyArrayRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
}
