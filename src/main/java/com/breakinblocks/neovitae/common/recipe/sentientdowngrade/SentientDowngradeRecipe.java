package com.breakinblocks.neovitae.common.recipe.sentientdowngrade;

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
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import javax.annotation.Nonnull;

/**
 * Recipe for applying sentient armor downgrades.
 * Takes an input item and applies a specific downgrade to sentient armor.
 */
public class SentientDowngradeRecipe implements Recipe<SentientDowngradeInput> {
    public static final String RECIPE_TYPE_NAME = "livingdowngrade";

    public static final MapCodec<SentientDowngradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SentientDowngradeRecipe::getInput),
            ResourceLocation.CODEC.fieldOf("livingarmour").forGetter(SentientDowngradeRecipe::getLivingUpgradeId)
    ).apply(instance, SentientDowngradeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SentientDowngradeRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SentientDowngradeRecipe::getInput,
            ResourceLocation.STREAM_CODEC, SentientDowngradeRecipe::getLivingUpgradeId,
            SentientDowngradeRecipe::new
    );

    @Nonnull
    private final Ingredient input;
    @Nonnull
    private final ResourceLocation livingUpgradeId;

    public SentientDowngradeRecipe(@Nonnull Ingredient input, @Nonnull ResourceLocation livingUpgradeId) {
        this.input = input;
        this.livingUpgradeId = livingUpgradeId;
    }

    @Nonnull
    public Ingredient getInput() {
        return input;
    }

    @Nonnull
    public ResourceLocation getLivingUpgradeId() {
        return livingUpgradeId;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(getInput());
        return list;
    }

    @Override
    public boolean matches(SentientDowngradeInput container, Level level) {
        return input.test(container.input());
    }

    @Override
    public ItemStack assemble(SentientDowngradeInput container, HolderLookup.Provider registries) {
        // Living downgrade recipes don't produce an item output
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.LIVING_DOWNGRADE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NVRecipes.LIVING_DOWNGRADE_TYPE.get();
    }
}
