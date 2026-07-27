// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.item.ItemAnointmentProvider;

public class AnointmentApplyRecipe implements SmithingRecipe {

    public static final MapCodec<AnointmentApplyRecipe> CODEC = MapCodec.unit(AnointmentApplyRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, AnointmentApplyRecipe> STREAM_CODEC =
            StreamCodec.unit(new AnointmentApplyRecipe());

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.getItem() instanceof ItemAnointmentProvider;
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return ItemAnointmentProvider.isItemTool(stack)
                || ItemAnointmentProvider.isItemSword(stack)
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem;
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return false;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().getItem() instanceof ItemAnointmentProvider provider
                && provider.canApplyToStack(input.base());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        if (input.template().getItem() instanceof ItemAnointmentProvider provider) {
            return provider.applyToStack(input.base());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.ANOINTMENT_APPLY_SERIALIZER.get();
    }
}
