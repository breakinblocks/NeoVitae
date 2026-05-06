package com.breakinblocks.neovitae.common.recipe.sentientdowngrade;

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
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import javax.annotation.Nonnull;

/**
 * Recipe for applying living armor downgrades.
 * Takes an input item and applies a specific downgrade to living armor.
 */
public class SentientDowngradeRecipe implements Recipe<SentientDowngradeInput> {
    public static final String RECIPE_TYPE_NAME = "sentientdowngrade";

    public static final MapCodec<SentientDowngradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(SentientDowngradeRecipe::getInput),
            Identifier.CODEC.fieldOf("livingarmour").forGetter(SentientDowngradeRecipe::getLivingUpgradeId)
    ).apply(instance, SentientDowngradeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SentientDowngradeRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SentientDowngradeRecipe::getInput,
            Identifier.STREAM_CODEC, SentientDowngradeRecipe::getLivingUpgradeId,
            SentientDowngradeRecipe::new
    );

    @Nonnull
    private final Ingredient input;
    @Nonnull
    private final Identifier livingUpgradeId;

    public SentientDowngradeRecipe(@Nonnull Ingredient input, @Nonnull Identifier livingUpgradeId) {
        this.input = input;
        this.livingUpgradeId = livingUpgradeId;
    }

    @Nonnull
    public Ingredient getInput() {
        return input;
    }

    @Nonnull
    public Identifier getLivingUpgradeId() {
        return livingUpgradeId;
    }

    @Override
    public boolean matches(SentientDowngradeInput container, Level level) {
        return input.test(container.input());
    }

    @Override
    public ItemStack assemble(SentientDowngradeInput container) {
        // Living downgrade recipes don't produce an item output
        return ItemStack.EMPTY;
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
        return PlacementInfo.create(input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<SentientDowngradeInput>> getSerializer() {
        return NVRecipes.SENTIENT_DOWNGRADE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<SentientDowngradeInput>> getType() {
        return NVRecipes.SENTIENT_DOWNGRADE_TYPE.get();
    }
}
