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

public class SigilDyeRecipe extends CustomRecipe {

    public static final MapCodec<SigilDyeRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> CraftingBookCategory.MISC)
    ).apply(builder, SigilDyeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SigilDyeRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, r -> CraftingBookCategory.MISC,
            SigilDyeRecipe::new
    );

    public SigilDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundSigil = false;
        boolean foundDye = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
                if (foundSigil) return false;
                foundSigil = true;
            } else if (stack.getItem() instanceof DyeItem) {
                if (foundDye) return false;
                foundDye = true;
            } else {
                return false;
            }
        }

        return foundSigil && foundDye;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack sigilStack = ItemStack.EMPTY;
        DyeColor dyeColor = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
                sigilStack = stack;
            } else if (stack.getItem() instanceof DyeItem dyeItem) {
                dyeColor = dyeItem.getDyeColor();
            }
        }

        if (sigilStack.isEmpty() || dyeColor == null) return ItemStack.EMPTY;

        ItemStack result = sigilStack.copy();
        result.set(NVDataComponents.BLOOD_LIGHT_COLOR.get(), dyeColor);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.SIGIL_DYE_SERIALIZER.get();
    }
}
