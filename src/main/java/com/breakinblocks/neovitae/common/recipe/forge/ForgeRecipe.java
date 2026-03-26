package com.breakinblocks.neovitae.common.recipe.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeRecipe implements Recipe<ForgeInput> {

    public static final String RECIPE_TYPE_NAME = "hellfire_forge";

    public static final MapCodec<ForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("minDrain").forGetter(ForgeRecipe::getMinWill),
            Codec.DOUBLE.fieldOf("drain").forGetter(ForgeRecipe::getDrain),
            Codec.list(Ingredient.CODEC_NONEMPTY).fieldOf("inputs").forGetter(ForgeRecipe::getCraftingIngredients),
            ItemStack.CODEC.fieldOf("output").forGetter(ForgeRecipe::getOutput),
            EnumWillType.CODEC.optionalFieldOf("willType").forGetter(ForgeRecipe::getWillType)
    ).apply(instance, ForgeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ForgeRecipe::getMinWill,
            ByteBufCodecs.DOUBLE, ForgeRecipe::getDrain,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ForgeRecipe::getCraftingIngredients,
            ItemStack.STREAM_CODEC, ForgeRecipe::getOutput,
            EnumWillType.STREAM_CODEC.apply(ByteBufCodecs::optional), ForgeRecipe::getWillType,
            ForgeRecipe::new
    );
    public final double minWill;
    public final double usedWill;
    public final List<Ingredient> ingredients;
    public final ItemStack resultItem;
    public final Optional<EnumWillType> willType;
    public ForgeRecipe(double minWill, double usedWill, List<Ingredient> ingredients, ItemStack resultItem, Optional<EnumWillType> willType) {
        this.minWill = minWill;
        this.usedWill = usedWill;
        this.ingredients = ingredients;
        this.resultItem = resultItem;
        this.willType = willType;
    }

    @Override
    public boolean matches(ForgeInput input, Level level) {
        if (input.size() != ingredients.size()) {
            return false;
        }
        EnumWillType will = input.getGem().getOrDefault(NVDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT);
        if (willType.isPresent() && willType.get() != will) {
            return false;
        }

        List<Ingredient> ingredientList = new ArrayList<>(ingredients);
        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            boolean matched = false;
            for (int j = 0; j < ingredientList.size(); j++) {
                if (ingredientList.get(j).test(stack)) {
                    matched = true;
                    ingredientList.remove(j);
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        return ingredientList.isEmpty();
    }

    @Override
    public ItemStack assemble(ForgeInput input, HolderLookup.Provider registries) {
        ItemStack gemStack = input.getGem();
        double will = gemStack.getOrDefault(NVDataComponents.DEMON_WILL_AMOUNT, 0D);
        if (will < minWill) {
            return ItemStack.EMPTY;
        }
        ItemStack outStack = resultItem.copy();
        if (outStack.is(NVTags.Items.SOUL_GEM) && input.getGemIndex() != HellfireForgeBlockEntity.GEM_SLOT) {
            outStack.set(NVDataComponents.DEMON_WILL_AMOUNT, will - usedWill);
            outStack.set(NVDataComponents.DEMON_WILL_TYPE, gemStack.get(NVDataComponents.DEMON_WILL_TYPE));
        }

        return outStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return resultItem.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.HELLFIRE_FORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NVRecipes.HELLFIRE_FORGE_TYPE.get();
    }

    public Double getMinWill() {
        return minWill;
    }

    public Double getDrain() {
        return usedWill;
    }

    public List<Ingredient> getCraftingIngredients() {
        return ingredients;
    }

    public ItemStack getOutput() {
        return resultItem;
    }

    public Optional<EnumWillType> getWillType() {
        return willType;
    }
}
