package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.fluid.NVFluids;

public final class OrbFluidHandler {
    private OrbFluidHandler() {}

    public static int getOrbFluidCapacity(ItemStack stack) {
        BloodOrb orb = stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (orb == null) return 0;
        return orb.fluidCapacity();
    }

    public static int getOrbFluidAmount(ItemStack stack) {
        SimpleFluidContent current = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        return current.isEmpty() ? 0 : current.getAmount();
    }

    public static int fillInternal(ItemStack stack, FluidStack fluid, boolean execute) {
        if (fluid.isEmpty() || fluid.getFluid() != NVFluids.ESSENTIA_VITAE_SOURCE.get()) return 0;

        int capacity = getOrbFluidCapacity(stack);
        if (capacity <= 0) return 0;

        int currentAmount = getOrbFluidAmount(stack);
        int space = capacity - currentAmount;
        int toFill = Math.min(space, fluid.getAmount());
        if (toFill <= 0) return 0;

        if (execute) {
            FluidStack newFluid = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), currentAmount + toFill);
            stack.set(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.copyOf(newFluid));
        }
        return toFill;
    }

    public static FluidStack drainInternal(ItemStack stack, int amount, boolean execute) {
        if (amount <= 0) return FluidStack.EMPTY;

        int currentAmount = getOrbFluidAmount(stack);
        int toDrain = Math.min(currentAmount, amount);
        if (toDrain <= 0) return FluidStack.EMPTY;

        FluidStack drained = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), toDrain);

        if (execute) {
            int remaining = currentAmount - toDrain;
            if (remaining <= 0) {
                stack.remove(NVDataComponents.ORB_FLUID.get());
            } else {
                FluidStack newFluid = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), remaining);
                stack.set(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.copyOf(newFluid));
            }
        }
        return drained;
    }
}
