package com.breakinblocks.neovitae.common.recipe.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.NVRecipeCodecs;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeRecipe implements Recipe<ForgeInput> {

    public static final String RECIPE_TYPE_NAME = "hellfire_forge";

    public static final MapCodec<ForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("minDrain").forGetter(ForgeRecipe::getMinSpiritus),
            Codec.DOUBLE.fieldOf("drain").forGetter(ForgeRecipe::getDrain),
            Codec.list(NVRecipeCodecs.INGREDIENT).fieldOf("inputs").forGetter(ForgeRecipe::getCraftingIngredients),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.resultTemplate),
            SpiritusType.CODEC.optionalFieldOf("spiritusType").forGetter(ForgeRecipe::getSpiritusType)
    ).apply(instance, ForgeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ForgeRecipe::getMinSpiritus,
            ByteBufCodecs.DOUBLE, ForgeRecipe::getDrain,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ForgeRecipe::getCraftingIngredients,
            ItemStackTemplate.STREAM_CODEC, r -> r.resultTemplate,
            SpiritusType.STREAM_CODEC.apply(ByteBufCodecs::optional), ForgeRecipe::getSpiritusType,
            ForgeRecipe::new
    );
    public final double minSpiritus;
    public final double usedSpiritus;
    public final List<Ingredient> ingredients;
    public final ItemStackTemplate resultTemplate;
    public final Optional<SpiritusType> spiritusType;
    public ForgeRecipe(double minSpiritus, double usedSpiritus, List<Ingredient> ingredients, ItemStackTemplate resultTemplate, Optional<SpiritusType> spiritusType) {
        this.minSpiritus = minSpiritus;
        this.usedSpiritus = usedSpiritus;
        this.ingredients = ingredients;
        this.resultTemplate = resultTemplate;
        this.spiritusType = spiritusType;
    }

    @Override
    public boolean matches(ForgeInput input, Level level) {
        if (input.size() != ingredients.size()) {
            return false;
        }
        SpiritusType spiritus = input.getGem().getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        if (spiritusType.isPresent() && spiritusType.get() != spiritus) {
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

    public boolean hasEnoughSpiritus(ForgeInput input) {
        return input.getGem().getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D) >= minSpiritus;
    }

    @Override
    public ItemStack assemble(ForgeInput input) {
        ItemStack gemStack = input.getGem();
        double spiritus = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
        if (spiritus < minSpiritus) {
            return ItemStack.EMPTY;
        }
        ItemStack outStack = resultTemplate.create();
        if (outStack.is(NVTags.Items.SPIRITUS_GEM) && input.getGemIndex() != HellfireForgeBlockEntity.GEM_SLOT) {
            outStack.set(NVDataComponents.SPIRITUS_AMOUNT, spiritus - usedSpiritus);
            outStack.set(NVDataComponents.SPIRITUS_TYPE, gemStack.get(NVDataComponents.SPIRITUS_TYPE));
        }

        return outStack;
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
        return PlacementInfo.createFromOptionals(ingredients.stream().map(Optional::of).toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<ForgeInput>> getSerializer() {
        return NVRecipes.HELLFIRE_FORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<ForgeInput>> getType() {
        return NVRecipes.HELLFIRE_FORGE_TYPE.get();
    }

    public Double getMinSpiritus() {
        return minSpiritus;
    }

    public Double getDrain() {
        return usedSpiritus;
    }

    public List<Ingredient> getCraftingIngredients() {
        return ingredients;
    }

    public ItemStack getOutput() {
        return resultTemplate.create();
    }

    public Optional<SpiritusType> getSpiritusType() {
        return spiritusType;
    }
}
