package com.breakinblocks.neovitae.common.recipe.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeTransformRecipe extends ForgeRecipe {

    public static final MapCodec<ForgeTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("minDrain").forGetter(r -> r.minWill),
            Codec.DOUBLE.fieldOf("drain").forGetter(r -> r.usedWill),
            Codec.list(Ingredient.CODEC_NONEMPTY).fieldOf("catalysts").forGetter(ForgeTransformRecipe::getCatalysts),
            Ingredient.CODEC_NONEMPTY.fieldOf("transformInput").forGetter(r -> r.transformInput),
            ItemStack.CODEC.fieldOf("output").forGetter(r -> r.resultItem)
    ).apply(instance, ForgeTransformRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeTransformRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, r -> r.minWill,
            ByteBufCodecs.DOUBLE, r -> r.usedWill,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ForgeTransformRecipe::getCatalysts,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.transformInput,
            ItemStack.STREAM_CODEC, r -> r.resultItem,
            ForgeTransformRecipe::new
    );

    private final Ingredient transformInput;
    private final List<Ingredient> catalysts;

    public ForgeTransformRecipe(double minWill, double usedWill, List<Ingredient> catalysts,
                                 Ingredient transformInput, ItemStack resultItem) {
        super(minWill, usedWill, combinedIngredients(catalysts, transformInput), resultItem, Optional.empty());
        this.transformInput = transformInput;
        this.catalysts = catalysts;
    }

    private static List<Ingredient> combinedIngredients(List<Ingredient> catalysts, Ingredient transformInput) {
        List<Ingredient> all = new ArrayList<>(catalysts);
        all.add(transformInput);
        return all;
    }

    public List<Ingredient> getCatalysts() {
        return catalysts;
    }

    public Ingredient getTransformInput() {
        return transformInput;
    }

    @Override
    public ItemStack assemble(ForgeInput input, HolderLookup.Provider registries) {
        ItemStack gemStack = input.getGem();
        double will = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
        if (will < minWill) return ItemStack.EMPTY;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !transformInput.test(stack)) continue;

            ItemStack result = resultItem.copy();

            if (stack.has(DataComponents.ENCHANTMENTS))
                result.set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS));
            if (stack.has(DataComponents.CUSTOM_NAME))
                result.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
            if (stack.has(DataComponents.REPAIR_COST))
                result.set(DataComponents.REPAIR_COST, stack.get(DataComponents.REPAIR_COST));
            if (stack.has(NVDataComponents.BLOOD_MENDING.get()))
                result.set(NVDataComponents.BLOOD_MENDING.get(), true);
            if (stack.has(NVDataComponents.ANOINTMENT_HOLDER.get()))
                result.set(NVDataComponents.ANOINTMENT_HOLDER.get(), stack.get(NVDataComponents.ANOINTMENT_HOLDER.get()));

            result.setDamageValue(0);
            return result;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.HELLFIRE_FORGE_TRANSFORM_SERIALIZER.get();
    }
}
