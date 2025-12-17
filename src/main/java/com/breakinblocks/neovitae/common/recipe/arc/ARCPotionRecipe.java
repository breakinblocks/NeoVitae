package com.breakinblocks.neovitae.common.recipe.arc;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ARC recipe variant that copies potion effects from the tool (lingering alchemy flask)
 * to the output item. Used for creating tipped throwing daggers.
 */
public class ARCPotionRecipe extends ARCRecipe {

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
                com.breakinblocks.neovitae.common.datacomponent.BMDataComponents.ARC_CHANCE, 1D);
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
        return BMRecipes.ARC_POTION_SERIALIZER.get();
    }
}
