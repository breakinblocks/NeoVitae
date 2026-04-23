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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
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

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStackTemplate, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    private Ingredient getSingleInput() {
        return getInputs().isEmpty() ? Ingredient.of() : getInputs().getFirst();
    }

    public static final MapCodec<AthanorPotionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(AthanorPotionRecipe::getTool),
            Ingredient.CODEC.fieldOf("input").forGetter(AthanorPotionRecipe::getSingleInput),
            ItemStackTemplate.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(AthanorPotionRecipe::getGuaranteedOutput),
            Codec.pair(ItemStackTemplate.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(AthanorPotionRecipe::getChanceOutput),
            SizedFluidIngredient.CODEC.optionalFieldOf("input_fluid").forGetter(AthanorPotionRecipe::getInputFluid),
            FluidStackTemplate.CODEC.optionalFieldOf("output_fluid").forGetter(AthanorPotionRecipe::getOutputFluidTemplate)
    ).apply(inst, AthanorPotionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AthanorPotionRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AthanorPotionRecipe::getTool,
            Ingredient.CONTENTS_STREAM_CODEC, AthanorPotionRecipe::getSingleInput,
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), AthanorPotionRecipe::getGuaranteedOutput,
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()), AthanorPotionRecipe::getChanceOutput,
            SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional), AthanorPotionRecipe::getInputFluid,
            FluidStackTemplate.STREAM_CODEC.apply(ByteBufCodecs::optional), AthanorPotionRecipe::getOutputFluidTemplate,
            AthanorPotionRecipe::new
    );

    public AthanorPotionRecipe(Ingredient tool, Ingredient input, List<ItemStackTemplate> guaranteedOutput,
                           List<Pair<ItemStackTemplate, Double>> chanceOutput,
                           Optional<SizedFluidIngredient> inputFluid, Optional<FluidStackTemplate> outputStack) {
        super(tool, List.of(input), guaranteedOutput, chanceOutput, inputFluid, outputStack, Map.of());
    }

    @Override
    public AthanorResult assembleOutputs(AthanorRecipeInput input) {
        List<ItemStack> outputs = new ArrayList<>(getGuaranteedOutput().size() + getChanceOutput().size());

        ItemStack toolStack = input.getItem(0);
        PotionContents toolContents = toolStack.get(DataComponents.POTION_CONTENTS);

        for (ItemStackTemplate guaranteedTpl : getGuaranteedOutput()) {
            ItemStack outputStack = guaranteedTpl.create();
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
                NVDataComponents.ARC_CHANCE, 1D);
        for (Pair<ItemStackTemplate, Double> entry : getChanceOutput()) {
            if (Math.random() < entry.getSecond() * bonusChance) {
                outputs.add(entry.getFirst().create());
            }
        }

        return new AthanorResult(outputs, getOutputFluid().orElse(FluidStack.EMPTY));
    }

    @Override
    public RecipeSerializer<? extends Recipe<AthanorRecipeInput>> getSerializer() {
        return NVRecipes.ATHANOR_POTION_SERIALIZER.get();
    }
}
