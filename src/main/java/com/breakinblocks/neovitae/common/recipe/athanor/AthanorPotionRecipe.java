package com.breakinblocks.neovitae.common.recipe.athanor;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Athanor recipe variant that copies potion effects from the tool (lingering alchemy flask)
 * to the output item. Used for creating tipped throwing daggers.
 * Always has exactly one input ingredient (serialized as singular "input" for compatibility).
 */
public class AthanorPotionRecipe extends AthanorRecipe {

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    private Ingredient getSingleInput() {
        return getInputs().isEmpty() ? Ingredient.of() : getInputs().getFirst();
    }

    public static final MapCodec<AthanorPotionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(AthanorPotionRecipe::getTool),
            Ingredient.CODEC.fieldOf("input").forGetter(AthanorPotionRecipe::getSingleInput),
            ItemStack.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(AthanorPotionRecipe::getGuaranteedOutput),
            Codec.pair(ItemStack.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(AthanorPotionRecipe::getChanceOutput),
            SizedFluidIngredient.CODEC.optionalFieldOf("input_fluid").forGetter(AthanorPotionRecipe::getInputFluid),
            FluidStack.CODEC.optionalFieldOf("output_fluid").forGetter(AthanorPotionRecipe::getOutputFluid)
    ).apply(inst, AthanorPotionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AthanorPotionRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AthanorPotionRecipe::getTool,
            Ingredient.CONTENTS_STREAM_CODEC, AthanorPotionRecipe::getSingleInput,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), AthanorPotionRecipe::getGuaranteedOutput,
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()), AthanorPotionRecipe::getChanceOutput,
            SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional), AthanorPotionRecipe::getInputFluid,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), AthanorPotionRecipe::getOutputFluid,
            AthanorPotionRecipe::new
    );

    public AthanorPotionRecipe(Ingredient tool, Ingredient input, List<ItemStack> guaranteedOutput,
                           List<Pair<ItemStack, Double>> chanceOutput,
                           Optional<SizedFluidIngredient> inputFluid, Optional<FluidStack> outputStack) {
        super(tool, List.of(input), guaranteedOutput, chanceOutput, inputFluid, outputStack, Map.of());
    }

    @Override
    public AthanorResult assembleOutputs(AthanorRecipeInput input) {
        List<ItemStack> outputs = new ArrayList<>(getGuaranteedOutput().size() + getChanceOutput().size());

        ItemStack toolStack = input.getItem(0);
        PotionContents toolContents = toolStack.get(DataComponents.POTION_CONTENTS);

        for (ItemStack guaranteedStack : getGuaranteedOutput()) {
            ItemStack outputStack = guaranteedStack.copy();
            if (toolContents != null && toolContents.hasEffects()) {
                List<MobEffectInstance> effects = new ArrayList<>();
                toolContents.getAllEffects().forEach(effect -> effects.add(new MobEffectInstance(effect)));
                PotionContents newContents = new PotionContents(
                        Optional.empty(),
                        Optional.empty(),
                        effects,
                        Optional.empty()
                );
                outputStack.set(DataComponents.POTION_CONTENTS, newContents);
            }
            outputs.add(outputStack);
        }

        double bonusChance = toolStack.getOrDefault(
                com.breakinblocks.neovitae.common.datacomponent.NVDataComponents.ARC_CHANCE, 1D);
        for (Pair<ItemStack, Double> entry : getChanceOutput()) {
            if (Math.random() < entry.getSecond() * bonusChance) {
                outputs.add(entry.getFirst().copy());
            }
        }

        return new AthanorResult(outputs, getOutputFluid().orElse(FluidStack.EMPTY).copy());
    }

    @Override
    public RecipeSerializer<? extends Recipe<AthanorRecipeInput>> getSerializer() {
        return NVRecipes.ATHANOR_POTION_SERIALIZER.get();
    }
}
