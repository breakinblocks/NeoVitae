package com.breakinblocks.neovitae.common.recipe.flask;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;

import java.util.List;

/**
 * Recipe input for flask recipes, containing ingredient items, the flask stack,
 * and the current list of effects in the flask.
 */
public record FlaskInput(
        List<ItemStack> items,
        ItemStack flaskStack,
        List<EffectHolder> flaskEffects,
        int orbTier
) implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < items.size()) {
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}
