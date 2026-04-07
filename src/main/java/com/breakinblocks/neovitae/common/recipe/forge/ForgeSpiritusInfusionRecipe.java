package com.breakinblocks.neovitae.common.recipe.forge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeSpiritusInfusionRecipe extends ForgeRecipe {

    public static final MapCodec<ForgeSpiritusInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("minDrain").forGetter(r -> r.minWill),
            Codec.DOUBLE.fieldOf("drain").forGetter(r -> r.usedWill),
            Ingredient.CODEC_NONEMPTY.fieldOf("gemInput").forGetter(r -> r.gemInput)
    ).apply(instance, ForgeSpiritusInfusionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeSpiritusInfusionRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, r -> r.minWill,
            ByteBufCodecs.DOUBLE, r -> r.usedWill,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.gemInput,
            ForgeSpiritusInfusionRecipe::new
    );

    private final Ingredient gemInput;

    public ForgeSpiritusInfusionRecipe(double minWill, double usedWill, Ingredient gemInput) {
        super(minWill, usedWill, List.of(gemInput), ItemStack.EMPTY, Optional.empty());
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

            if (!foundTarget && stack.is(NVTags.Items.SPIRITUS_CAPABLE) && !stack.has(NVDataComponents.SPIRITUS_MAX)) {
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
        if (will < minWill) return ItemStack.EMPTY;

        Double gemMax = null;
        ItemStack targetStack = null;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (gemMax == null && gemInput.test(stack)) {
                gemMax = stack.getItemHolder().getData(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS);
                continue;
            }

            if (targetStack == null && stack.is(NVTags.Items.SPIRITUS_CAPABLE)) {
                targetStack = stack;
            }
        }

        if (gemMax == null || targetStack == null) return ItemStack.EMPTY;

        ItemStack result = targetStack.copy();
        result.set(NVDataComponents.SPIRITUS_MAX, gemMax);
        result.set(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        result.set(NVDataComponents.SPIRITUS_TYPE, com.breakinblocks.neovitae.common.datacomponent.SpiritusType.DEFAULT);
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.HELLFIRE_FORGE_SPIRITUS_INFUSION_SERIALIZER.get();
    }
}
