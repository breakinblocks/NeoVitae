package com.breakinblocks.neovitae.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.item.BMItems;

/**
 * Custom recipe that combines two Upgrade Tomes with the same upgrade type.
 * The resulting tome has the combined exp of both input tomes.
 */
public class UpgradeTomeCombineRecipe extends CustomRecipe {

    public UpgradeTomeCombineRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        UpgradeTome firstTome = null;
        int tomeCount = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (!stack.is(BMItems.UPGRADE_TOME.get())) {
                return false; // Non-tome item in grid
            }

            UpgradeTome tome = stack.get(BMDataComponents.UPGRADE_TOME_DATA);
            if (tome == null) {
                return false; // Tome without data
            }

            if (firstTome == null) {
                firstTome = tome;
            } else if (!tome.upgrade().equals(firstTome.upgrade())) {
                return false; // Different upgrade types
            }

            tomeCount++;
        }

        return tomeCount == 2; // Exactly 2 tomes required
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        UpgradeTome firstTome = null;
        float totalExp = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            UpgradeTome tome = stack.get(BMDataComponents.UPGRADE_TOME_DATA);
            if (tome != null) {
                if (firstTome == null) {
                    firstTome = tome;
                }
                totalExp += tome.exp();
            }
        }

        if (firstTome == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(BMItems.UPGRADE_TOME.get());
        result.set(BMDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(firstTome.upgrade(), totalExp));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BMRecipes.UPGRADE_TOME_COMBINE_SERIALIZER.get();
    }
}
