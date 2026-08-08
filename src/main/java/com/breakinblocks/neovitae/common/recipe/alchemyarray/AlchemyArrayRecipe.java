// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.recipe.alchemyarray;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.recipe.NVRecipeCodecs;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AlchemyArrayRecipe implements Recipe<AlchemyArrayInput> {
    public static final String RECIPE_TYPE_NAME = "array";

    public static final MapCodec<AlchemyArrayRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(AlchemyArrayRecipe::getTexture),
            NVRecipeCodecs.INGREDIENT.fieldOf("baseinput").forGetter(AlchemyArrayRecipe::getBaseInput),
            NVRecipeCodecs.INGREDIENT.fieldOf("addedinput").forGetter(AlchemyArrayRecipe::getAddedInput),
            ItemStackTemplate.CODEC.optionalFieldOf("output").forGetter(r -> Optional.ofNullable(r.getOutputTemplate())),
            AlchemyArrayEffectType.CODEC.optionalFieldOf("effect_type", AlchemyArrayEffectType.CRAFTING).forGetter(AlchemyArrayRecipe::getEffectType),
            Codec.INT.optionalFieldOf("ev_cost", 0).forGetter(AlchemyArrayRecipe::getEvCost)
    ).apply(instance, (tex, base, added, out, effect, evCost) -> new AlchemyArrayRecipe(tex, base, added, out.orElse(null), effect, evCost)));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyArrayRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AlchemyArrayRecipe::getTexture,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getBaseInput,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getAddedInput,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), r -> Optional.ofNullable(r.getOutputTemplate()),
            AlchemyArrayEffectType.STREAM_CODEC, AlchemyArrayRecipe::getEffectType,
            ByteBufCodecs.VAR_INT, AlchemyArrayRecipe::getEvCost,
            (tex, base, added, out, effect, evCost) -> new AlchemyArrayRecipe(tex, base, added, out.orElse(null), effect, evCost)
    );

    private final Identifier texture;
    @Nonnull
    private final Ingredient baseInput;
    @Nonnull
    private final Ingredient addedInput;
    @Nullable
    private final ItemStackTemplate outputTemplate;
    @Nonnull
    private final AlchemyArrayEffectType effectType;
    private final int evCost;

    public AlchemyArrayRecipe(Identifier texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nullable ItemStackTemplate result) {
        this(texture, baseIngredient, addedIngredient, result, AlchemyArrayEffectType.CRAFTING, 0);
    }

    public AlchemyArrayRecipe(Identifier texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nullable ItemStackTemplate result, @Nonnull AlchemyArrayEffectType effectType) {
        this(texture, baseIngredient, addedIngredient, result, effectType, 0);
    }

    public AlchemyArrayRecipe(Identifier texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nullable ItemStackTemplate result, @Nonnull AlchemyArrayEffectType effectType, int evCost) {
        this.texture = texture;
        this.baseInput = baseIngredient;
        this.addedInput = addedIngredient;
        this.outputTemplate = result;
        this.effectType = effectType;
        this.evCost = evCost;
    }

    @Nonnull
    public Identifier getTexture() {
        return texture;
    }

    @Nonnull
    public Ingredient getBaseInput() {
        return baseInput;
    }

    @Nonnull
    public Ingredient getAddedInput() {
        return addedInput;
    }

    @Nonnull
    public ItemStack getOutput() {
        return outputTemplate == null ? ItemStack.EMPTY : outputTemplate.create();
    }

    @Nullable
    public ItemStackTemplate getOutputTemplate() {
        return outputTemplate;
    }

    @Nonnull
    public AlchemyArrayEffectType getEffectType() {
        return effectType;
    }

    public int getEvCost() {
        return evCost;
    }

    @Override
    public boolean matches(AlchemyArrayInput input, Level level) {
        return baseInput.test(input.base()) && addedInput.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(AlchemyArrayInput input) {
        return getOutput();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.createFromOptionals(List.of(
                Optional.of(baseInput),
                Optional.of(addedInput)));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<AlchemyArrayInput>> getSerializer() {
        return NVRecipes.ALCHEMY_ARRAY_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<AlchemyArrayInput>> getType() {
        return NVRecipes.ALCHEMY_ARRAY_TYPE.get();
    }
}
