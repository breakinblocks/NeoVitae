package com.breakinblocks.neovitae.common.recipe.alchemyarray;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;

import javax.annotation.Nonnull;

public class AlchemyArrayRecipe implements Recipe<AlchemyArrayInput> {
    public static final String RECIPE_TYPE_NAME = "array";

    public static final MapCodec<AlchemyArrayRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(AlchemyArrayRecipe::getTexture),
            Ingredient.CODEC_NONEMPTY.fieldOf("baseinput").forGetter(AlchemyArrayRecipe::getBaseInput),
            Ingredient.CODEC_NONEMPTY.fieldOf("addedinput").forGetter(AlchemyArrayRecipe::getAddedInput),
            ItemStack.CODEC.optionalFieldOf("output", ItemStack.EMPTY).forGetter(AlchemyArrayRecipe::getOutput),
            AlchemyArrayEffectType.CODEC.optionalFieldOf("effect_type", AlchemyArrayEffectType.CRAFTING).forGetter(AlchemyArrayRecipe::getEffectType)
    ).apply(instance, AlchemyArrayRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyArrayRecipe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, AlchemyArrayRecipe::getTexture,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getBaseInput,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemyArrayRecipe::getAddedInput,
            ItemStack.OPTIONAL_STREAM_CODEC, AlchemyArrayRecipe::getOutput,
            AlchemyArrayEffectType.STREAM_CODEC, AlchemyArrayRecipe::getEffectType,
            AlchemyArrayRecipe::new
    );

    private final ResourceLocation texture;
    @Nonnull
    private final Ingredient baseInput;
    @Nonnull
    private final Ingredient addedInput;
    @Nonnull
    private final ItemStack output;
    @Nonnull
    private final AlchemyArrayEffectType effectType;

    public AlchemyArrayRecipe(ResourceLocation texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nonnull ItemStack result) {
        this(texture, baseIngredient, addedIngredient, result, AlchemyArrayEffectType.CRAFTING);
    }

    public AlchemyArrayRecipe(ResourceLocation texture, @Nonnull Ingredient baseIngredient, @Nonnull Ingredient addedIngredient, @Nonnull ItemStack result, @Nonnull AlchemyArrayEffectType effectType) {
        this.texture = texture;
        this.baseInput = baseIngredient;
        this.addedInput = addedIngredient;
        this.output = result;
        this.effectType = effectType;
    }

    @Nonnull
    public ResourceLocation getTexture() {
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

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(getBaseInput());
        list.add(getAddedInput());
        return list;
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
    public ItemStack assemble(AlchemyArrayInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.ALCHEMY_ARRAY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NVRecipes.ALCHEMY_ARRAY_TYPE.get();
    }
}
