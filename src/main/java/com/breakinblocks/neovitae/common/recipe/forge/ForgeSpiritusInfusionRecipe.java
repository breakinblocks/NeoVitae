package com.breakinblocks.neovitae.common.recipe.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.recipe.NVRecipeCodecs;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeSpiritusInfusionRecipe extends ForgeRecipe {

    public static final MapCodec<ForgeSpiritusInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("minDrain").forGetter(r -> r.minSpiritus),
            Codec.DOUBLE.fieldOf("drain").forGetter(r -> r.usedSpiritus),
            NVRecipeCodecs.INGREDIENT.fieldOf("gemInput").forGetter(r -> r.gemInput)
    ).apply(instance, ForgeSpiritusInfusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeSpiritusInfusionRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, r -> r.minSpiritus,
            ByteBufCodecs.DOUBLE, r -> r.usedSpiritus,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.gemInput,
            ForgeSpiritusInfusionRecipe::new
    );

    private final Ingredient gemInput;

    public ForgeSpiritusInfusionRecipe(double minSpiritus, double usedSpiritus, Ingredient gemInput) {
        super(minSpiritus, usedSpiritus, List.of(gemInput), ItemStack.EMPTY, Optional.empty());
        this.gemInput = gemInput;
    }

    public Ingredient getGemInput() {
        return gemInput;
    }

    @Override
    public boolean matches(ForgeInput input, Level level) {
        if (input.size() != 2) return false;

        boolean foundGem = false;
        boolean foundTarget = false;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (!foundGem && gemInput.test(stack)) {
                Double gemMax = stack.getItemHolder().getData(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS);
                if (gemMax != null && gemMax > 0) {
                    foundGem = true;
                    continue;
                }
            }

            if (!foundTarget && isInfusionTarget(stack)) {
                foundTarget = true;
                continue;
            }

            return false;
        }

        return foundGem && foundTarget;
    }

    @Override
    public ItemStack assemble(ForgeInput input, HolderLookup.Provider registries) {
        ItemStack gemStack = input.getGem();
        double will = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
        if (will < minSpiritus) return ItemStack.EMPTY;

        Double gemMax = null;
        ItemStack targetStack = null;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (gemMax == null && gemInput.test(stack)) {
                gemMax = stack.getItemHolder().getData(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS);
                continue;
            }

            if (targetStack == null && isInfusionTarget(stack)) {
                targetStack = stack;
            }
        }

        if (gemMax == null || targetStack == null) return ItemStack.EMPTY;

        ItemStack result = targetStack.copy();
        result.set(NVDataComponents.SPIRITUS_MAX, gemMax);
        result.set(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        result.set(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        return result;
    }

    private static boolean isInfusionTarget(ItemStack stack) {
        return stack.is(NVTags.Items.SPIRITUS_CAPABLE)
                && !stack.has(NVDataComponents.SPIRITUS_MAX)
                && !stack.is(ItemTags.SWORDS)
                && !stack.is(ItemTags.AXES)
                && !stack.is(ItemTags.PICKAXES)
                && !stack.is(ItemTags.SHOVELS)
                && !stack.is(ItemTags.HOES);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.HELLFIRE_FORGE_SPIRITUS_INFUSION_SERIALIZER.get();
    }
}
