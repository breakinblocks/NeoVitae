package com.breakinblocks.neovitae.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;

public class SigilColorResetRecipe extends CustomRecipe {

    public static final MapCodec<SigilColorResetRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> CraftingBookCategory.MISC)
    ).apply(builder, SigilColorResetRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SigilColorResetRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, r -> CraftingBookCategory.MISC,
            SigilColorResetRecipe::new
    );

    public SigilColorResetRecipe(CraftingBookCategory category) {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean foundSigil = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
                if (foundSigil) return false;
                foundSigil = true;
            } else {
                return false;
            }
        }

        return foundSigil;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
                ItemStack result = stack.copy();
                result.set(NVDataComponents.BLOOD_LIGHT_COLOR.get(), DyeColor.RED);
                result.remove(NVDataComponents.BLOOD_LIGHT_RAINBOW.get());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = CraftingRecipe.defaultCraftingReminder(input);
        for (int i = 0; i < input.size(); i++) {
            if (input.getItem(i).is(NVItems.SIGIL_BLOOD_LIGHT.get())) {
                remaining.set(i, ItemStack.EMPTY);
            }
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return NVRecipes.SIGIL_COLOR_RESET_SERIALIZER.get();
    }
}
