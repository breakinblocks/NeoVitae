package com.breakinblocks.neovitae.common.recipe.tabulavitae;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.RecipeSerializerUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class TabulaVitaeRecipe implements Recipe<TabulaVitaeInput> {
    public static final String RECIPE_TYPE_NAME = "alchemytable";
    public static final int MAX_INPUTS = 6;

    public static final MapCodec<TabulaVitaeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf().fieldOf("input").forGetter(TabulaVitaeRecipe::getInput),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(TabulaVitaeRecipe::getOutputTemplate),
            Codec.INT.fieldOf("syphon").forGetter(TabulaVitaeRecipe::getSyphon),
            Codec.INT.fieldOf("ticks").forGetter(TabulaVitaeRecipe::getTicks),
            Codec.INT.fieldOf("upgradeLevel").forGetter(TabulaVitaeRecipe::getMinimumTier)
    ).apply(instance, TabulaVitaeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TabulaVitaeRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeSerializerUtils.INGREDIENT_LIST_CODEC, TabulaVitaeRecipe::getInput,
            ItemStackTemplate.STREAM_CODEC, TabulaVitaeRecipe::getOutputTemplate,
            ByteBufCodecs.INT, TabulaVitaeRecipe::getSyphon,
            ByteBufCodecs.INT, TabulaVitaeRecipe::getTicks,
            ByteBufCodecs.INT, TabulaVitaeRecipe::getMinimumTier,
            TabulaVitaeRecipe::new
    );

    @Nonnull
    protected final List<Ingredient> input;
    @Nonnull
    private final ItemStackTemplate outputTemplate;
    @Nonnegative
    private final int syphon;
    @Nonnegative
    private final int ticks;
    @Nonnegative
    private final int minimumTier;

    public TabulaVitaeRecipe(List<Ingredient> input, @Nonnull ItemStackTemplate output, int syphon, int ticks, int minimumTier) {
        Preconditions.checkNotNull(input, "input cannot be null.");
        Preconditions.checkNotNull(output, "output cannot be null.");
        Preconditions.checkArgument(syphon >= 0, "syphon cannot be negative.");
        Preconditions.checkArgument(ticks >= 0, "ticks cannot be negative.");
        Preconditions.checkArgument(minimumTier >= 0, "minimumTier cannot be negative.");

        this.input = input;
        this.outputTemplate = output;
        this.syphon = syphon;
        this.ticks = ticks;
        this.minimumTier = minimumTier;
    }

    @Nonnull
    public List<Ingredient> getInput() {
        return input;
    }

    @Nonnull
    public ItemStack getOutput() {
        return outputTemplate.create();
    }

    @Nonnull
    public ItemStackTemplate getOutputTemplate() {
        return outputTemplate;
    }

    public int getSyphon() {
        return syphon;
    }

    public int getTicks() {
        return ticks;
    }

    public int getMinimumTier() {
        return minimumTier;
    }

    @Override
    public boolean matches(TabulaVitaeInput container, Level level) {
        List<ItemStack> inputItems = container.items();

        List<Ingredient> remainingIngredients = new ArrayList<>(input);

        for (ItemStack stack : inputItems) {
            if (stack.isEmpty()) continue;

            // Try to match this stack with one of the remaining ingredients
            boolean matched = false;
            for (int i = 0; i < remainingIngredients.size(); i++) {
                if (remainingIngredients.get(i).test(stack)) {
                    remainingIngredients.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                // Extra item that doesn't match any ingredient
                return false;
            }
        }

        // All ingredients should be matched
        return remainingIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(TabulaVitaeInput container) {
        return outputTemplate.create();
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
        return PlacementInfo.createFromOptionals(input.stream().map(Optional::of).toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<TabulaVitaeInput>> getSerializer() {
        return NVRecipes.TABULA_VITAE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<TabulaVitaeInput>> getType() {
        return NVRecipes.TABULA_VITAE_TYPE.get();
    }
}
