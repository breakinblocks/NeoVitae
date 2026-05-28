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
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeUpgradeRecipe extends ForgeRecipe {

    public static final MapCodec<ForgeUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("minDrain").forGetter(r -> r.minSpiritus),
            Codec.DOUBLE.fieldOf("drain").forGetter(r -> r.usedSpiritus),
            Codec.list(Ingredient.CODEC_NONEMPTY).fieldOf("catalysts").forGetter(r -> r.ingredients)
    ).apply(instance, ForgeUpgradeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgeUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, r -> r.minSpiritus,
            ByteBufCodecs.DOUBLE, r -> r.usedSpiritus,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
            ForgeUpgradeRecipe::new
    );

    public ForgeUpgradeRecipe(double minSpiritus, double usedSpiritus, List<Ingredient> catalysts) {
        super(minSpiritus, usedSpiritus, catalysts, ItemStack.EMPTY, Optional.empty());
    }

    @Override
    public boolean matches(ForgeInput input, Level level) {
        if (input.size() != ingredients.size() + 1) {
            return false;
        }

        List<Ingredient> remaining = new ArrayList<>(ingredients);
        boolean foundTarget = false;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            boolean matchedCatalyst = false;
            for (int j = 0; j < remaining.size(); j++) {
                if (remaining.get(j).test(stack)) {
                    remaining.remove(j);
                    matchedCatalyst = true;
                    break;
                }
            }

            if (!matchedCatalyst) {
                if (foundTarget) return false;
                if (!stack.isDamageableItem()) return false;
                if (stack.is(NVTags.Items.BLOOD_MENDING_BLACKLIST)) return false;
                if (stack.has(NVDataComponents.BLOOD_MENDING.get())) return false;
                foundTarget = true;
            }
        }

        return remaining.isEmpty() && foundTarget;
    }

    @Override
    public ItemStack assemble(ForgeInput input, HolderLookup.Provider registries) {
        ItemStack gemStack = input.getGem();
        double will = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
        if (will < minSpiritus) return ItemStack.EMPTY;

        for (int i = 0; i < 4; i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty() || !stack.isDamageableItem()) continue;
            if (stack.has(NVDataComponents.BLOOD_MENDING.get())) continue;

            boolean isCatalyst = false;
            for (Ingredient ingredient : ingredients) {
                if (ingredient.test(stack)) {
                    isCatalyst = true;
                    break;
                }
            }

            if (!isCatalyst) {
                ItemStack result = stack.copy();
                result.set(NVDataComponents.BLOOD_MENDING.get(), true);
                return result;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.HELLFIRE_FORGE_UPGRADE_SERIALIZER.get();
    }
}
