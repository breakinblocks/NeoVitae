// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleSmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.item.ItemAnointmentProvider;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.List;
import java.util.Optional;

public class AnointmentApplyRecipe extends SimpleSmithingRecipe {

    public static final MapCodec<AnointmentApplyRecipe> CODEC = MapCodec.unit(AnointmentApplyRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, AnointmentApplyRecipe> STREAM_CODEC =
            StreamCodec.of((buf, recipe) -> {}, buf -> new AnointmentApplyRecipe());

    public AnointmentApplyRecipe() {
        super(new Recipe.CommonInfo(false));
    }

    @Override
    public Optional<Ingredient> templateIngredient() {
        return Optional.of(tagIngredient(NVTags.Items.ANOINTMENTS));
    }

    @Override
    public Ingredient baseIngredient() {
        return tagIngredient(NVTags.Items.ANOINTABLE);
    }

    @Override
    public Optional<Ingredient> additionIngredient() {
        return Optional.empty();
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().getItem() instanceof ItemAnointmentProvider provider
                && provider.canApplyToStack(input.base());
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        if (input.template().getItem() instanceof ItemAnointmentProvider provider) {
            return provider.applyToStack(input.base());
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(List.of(templateIngredient(), Optional.of(baseIngredient()), additionIngredient()));
    }

    @Override
    public RecipeSerializer<AnointmentApplyRecipe> getSerializer() {
        return NVRecipes.ANOINTMENT_APPLY_SERIALIZER.get();
    }

    private static Ingredient tagIngredient(TagKey<Item> tag) {
        return Ingredient.of(BuiltInRegistries.ITEM.get(tag).orElseThrow());
    }
}
