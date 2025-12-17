package com.breakinblocks.neovitae.common.recipe.flask;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Recipe that refills an empty/damaged flask with existing effects.
 * This restores durability (uses) to the flask.
 */
public class FlaskFillRecipe extends FlaskRecipe {

    private final int maxEffects;

    public FlaskFillRecipe(List<Ingredient> input, int maxEffects, int syphon, int ticks, int minimumTier) {
        super(input, syphon, ticks, minimumTier);
        this.maxEffects = maxEffects;
    }

    public int getMaxEffects() {
        return maxEffects;
    }

    @Override
    public boolean canModifyFlask(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        // Can only fill a flask that has effects
        return !flaskEffects.isEmpty();
    }

    @Override
    public int getPriority(List<EffectHolder> flaskEffects) {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack flaskStack, List<EffectHolder> flaskEffects) {
        ItemStack copyStack = flaskStack.copy();

        // Limit effects to maxEffects
        List<EffectHolder> limitedEffects = new ArrayList<>();
        for (int i = 0; i < Math.min(flaskEffects.size(), maxEffects); i++) {
            limitedEffects.add(flaskEffects.get(i));
        }

        ItemAlchemyFlask.setFlaskEffects(copyStack, new FlaskEffects(limitedEffects));

        // Reset durability
        copyStack.setDamageValue(0);

        return copyStack;
    }

    @Nonnull
    @Override
    public ItemStack getExampleFlask() {
        ItemStack flaskStack = new ItemStack(BMItems.ALCHEMY_FLASK.get());
        // Show damaged flask with effects for fill recipe
        flaskStack.setDamageValue(8);
        List<EffectHolder> exampleEffects = getExampleEffects();
        if (!exampleEffects.isEmpty()) {
            ItemAlchemyFlask.setFlaskEffects(flaskStack, new FlaskEffects(exampleEffects));
        }
        return flaskStack;
    }

    @Override
    public List<EffectHolder> getExampleEffects() {
        return createDefaultExampleEffects();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.FLASK_FILL_SERIALIZER.get();
    }
}
