package com.breakinblocks.neovitae.common.recipe.alchemyarray;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class AlchemyArrayRecipe implements Recipe<AlchemyArrayInput> {
    public static final String RECIPE_TYPE_NAME = "array";

    public static final MapCodec<AlchemyArrayRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(AlchemyArrayRecipe::getTexture),
            Ingredient.CODEC.fieldOf("baseinput").forGetter(AlchemyArrayRecipe::getBaseInput),
            Ingredient.CODEC.fieldOf("addedinput").forGetter(AlchemyArrayRecipe::getAddedInput),
            ItemStack.CODEC.optionalFieldOf("output", ItemStack.EMPTY).forGetter(AlchemyArrayRecipe::getOutput),
            AlchemyArrayEffectType.CODEC.optionalFieldOf("effect_type", AlchemyArrayEffectType.CRAFTING).forGetter(AlchemyArrayRecipe::getEffectType)
    ).apply(instance, AlchemyArrayRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyArrayRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AlchemyArrayRecipe::getTexture,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getBaseInput,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getAddedInput,
            ItemStack.OPTIONAL_STREAM_CODEC, AlchemyArrayRecipe::getOutput,
            AlchemyArrayEffectType.STREAM_CODEC, AlchemyArrayRecipe::getEffectType,
            AlchemyArrayRecipe::new
    );

    private final Identifier texture;
    @Nonnull
    private final Ingredient baseInput;
    @Nonnull
    private final Ingredient addedInput;
    @Nonnull
    private final ItemStack output;
    @Nonnull
    private final AlchemyArrayEffectType effectType;

    public AlchemyArrayRecipe(Identifier texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nonnull ItemStack result) {
        this(texture, baseIngredient, addedIngredient, result, AlchemyArrayEffectType.CRAFTING);
    }

    public AlchemyArrayRecipe(Identifier texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nonnull ItemStack result, @Nonnull AlchemyArrayEffectType effectType) {
        this.texture = texture;
        this.baseInput = baseIngredient;
        this.addedInput = addedIngredient;
        this.output = result;
        this.effectType = effectType;
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
        return output;
    }

    @Nonnull
    public AlchemyArrayEffectType getEffectType() {
        return effectType;
    }

    @Override
    public boolean matches(AlchemyArrayInput input, Level level) {
        return baseInput.test(input.base()) && addedInput.test(input.catalyst());
    }

    @Override
    public ItemStack assemble(AlchemyArrayInput input) {
        return output.copy();
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
