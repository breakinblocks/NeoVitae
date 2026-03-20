package com.breakinblocks.neovitae.common.recipe.arc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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
import com.breakinblocks.neovitae.common.recipe.NVRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ARC recipe variant that copies potion effects from the tool (lingering alchemy flask)
 * to the output item. Used for creating tipped throwing daggers.
 */
public class ARCPotionRecipe extends ARCRecipe {

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    public static final MapCodec<ARCPotionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(ARCPotionRecipe::getTool),
            Ingredient.CODEC.fieldOf("input").forGetter(ARCPotionRecipe::getInput),
            ItemStack.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(ARCPotionRecipe::getGuaranteedOutput),
            Codec.pair(ItemStack.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(ARCPotionRecipe::getChanceOutput),
            FluidStack.CODEC.optionalFieldOf("input_fluid").forGetter(ARCPotionRecipe::getInputFluid),
            FluidStack.CODEC.optionalFieldOf("output_fluid").forGetter(ARCPotionRecipe::getOutputFluid)
    ).apply(inst, ARCPotionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ARCPotionRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ARCPotionRecipe::getTool,
            Ingredient.CONTENTS_STREAM_CODEC, ARCPotionRecipe::getInput,
            ItemStack.LIST_STREAM_CODEC, ARCPotionRecipe::getGuaranteedOutput,
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()), ARCPotionRecipe::getChanceOutput,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), ARCPotionRecipe::getInputFluid,
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional), ARCPotionRecipe::getOutputFluid,
            ARCPotionRecipe::new
    );

    public ARCPotionRecipe(Ingredient tool, Ingredient input, List<ItemStack> guaranteedOutput,
                           List<Pair<ItemStack, Double>> chanceOutput,
                           Optional<FluidStack> inputFluid, Optional<FluidStack> outputStack) {
        super(tool, input, guaranteedOutput, chanceOutput, inputFluid, outputStack);
    }

    private List<ItemStack> outputStacks = new ArrayList<>();
    private FluidStack outputFluidStack = FluidStack.EMPTY;

    @Override
    public ItemStack assemble(ARCRecipeInput input, HolderLookup.Provider registries) {
        outputStacks.clear();
        outputFluidStack = getOutputFluid().orElse(FluidStack.EMPTY);

        ItemStack toolStack = input.getItem(0);

        // Get potion effects from the tool (lingering flask)
        PotionContents toolContents = toolStack.get(DataComponents.POTION_CONTENTS);

        // Copy guaranteed outputs with potion effects applied
        for (ItemStack guaranteedStack : getGuaranteedOutput()) {
            ItemStack outputStack = guaranteedStack.copy();
            if (toolContents != null && toolContents.hasEffects()) {
                // Transfer potion effects to output
                List<MobEffectInstance> effects = new ArrayList<>();
                toolContents.getAllEffects().forEach(effect -> effects.add(new MobEffectInstance(effect)));
                PotionContents newContents = new PotionContents(
                        Optional.empty(),
                        Optional.empty(),
                        effects
                );
                outputStack.set(DataComponents.POTION_CONTENTS, newContents);
            }
            outputStacks.add(outputStack);
        }

        // Process chanced outputs (without potion effects for simplicity)
        double bonusChance = input.getItem(0).getOrDefault(
                com.breakinblocks.neovitae.common.datacomponent.NVDataComponents.ARC_CHANCE, 1D);
        for (Pair<ItemStack, Double> entry : getChanceOutput()) {
            if (Math.random() < entry.getSecond() * bonusChance) {
                outputStacks.add(entry.getFirst().copy());
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public List<ItemStack> getActualOutputs() {
        return outputStacks;
    }

    @Override
    public FluidStack getActualOutputFluid() {
        return outputFluidStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.ARC_POTION_SERIALIZER.get();
    }
}
