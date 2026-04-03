package com.breakinblocks.neovitae.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;

public class ArcaneScribeDyeRecipe extends CustomRecipe {

    public static final MapCodec<ArcaneScribeDyeRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> CraftingBookCategory.MISC)
    ).apply(builder, ArcaneScribeDyeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArcaneScribeDyeRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, r -> CraftingBookCategory.MISC,
            ArcaneScribeDyeRecipe::new
    );

    public ArcaneScribeDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundScribe = false;
        boolean foundDye = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(NVItems.ARCANE_SCRIBE_TOOL.get())) {
                if (foundScribe) return false;
                foundScribe = true;
            } else if (stack.getItem() instanceof DyeItem) {
                if (foundDye) return false;
                foundDye = true;
            } else {
                return false;
            }
        }

        return foundScribe && foundDye;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack scribeStack = ItemStack.EMPTY;
        DyeColor dyeColor = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(NVItems.ARCANE_SCRIBE_TOOL.get())) {
                scribeStack = stack;
            } else if (stack.getItem() instanceof DyeItem dyeItem) {
                dyeColor = dyeItem.getDyeColor();
            }
        }

        if (scribeStack.isEmpty() || dyeColor == null) return ItemStack.EMPTY;

        ItemStack result = scribeStack.copy();
        result.set(NVDataComponents.ALCHEMY_ARRAY_COLOR.get(), dyeColor);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.ARCANE_SCRIBE_DYE_SERIALIZER.get();
    }
}
