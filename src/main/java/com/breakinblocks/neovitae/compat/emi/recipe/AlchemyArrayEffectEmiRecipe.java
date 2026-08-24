package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AlchemyArrayEffectEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/binding.png");

    private final AlchemyArrayRecipe recipe;

    public AlchemyArrayEffectEmiRecipe(AlchemyArrayRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.ALCHEMY_ARRAY_EFFECT, id, 100, 30);
        this.recipe = recipe;
        this.inputs = List.of(EmiIngredient.of(recipe.getBaseInput()), EmiIngredient.of(recipe.getAddedInput()));
        ItemStack dummy = effectDummy(recipe.getEffectType());
        this.outputs = dummy.isEmpty() ? List.of() : List.of(EmiStack.of(dummy));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 100, 30, 0, 0);
        widgets.addSlot(inputs.get(0), 0, 5).drawBack(false);
        widgets.addSlot(inputs.get(1), 29, 3).drawBack(false);
        if (!outputs.isEmpty()) {
            widgets.addSlot(outputs.get(0), 73, 5).drawBack(false).recipeContext(this);
        }

        ResourceLocation arrayTexture = recipe.getTexture();
        widgets.addDrawable(0, 0, 100, 30, (graphics, mouseX, mouseY, delta) ->
                graphics.blit(arrayTexture, 74, 6, 0, 0, 16, 16, 16, 16));

        String key = recipe.getEffectType().getSerializedName();
        widgets.addTooltipText(List.of(
                Component.translatable("jei.neovitae.effect." + key + ".name"),
                Component.translatable("jei.neovitae.effect." + key + ".desc")
        ), 70, 2, 25, 25);
    }

    private static ItemStack effectDummy(AlchemyArrayEffectType type) {
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
}
