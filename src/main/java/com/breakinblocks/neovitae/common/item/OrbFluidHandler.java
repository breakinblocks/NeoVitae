package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.fluid.NVFluids;

public class OrbFluidHandler implements IFluidHandlerItem {
    private final ItemStack stack;

    public OrbFluidHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getContainer() {
        return stack;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        SimpleFluidContent content = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        return content.copy();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getOrbFluidCapacity(stack);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack fluidStack) {
        return fluidStack.getFluid() == NVFluids.ESSENTIA_VITAE_SOURCE.get();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    public static int getOrbFluidCapacity(ItemStack stack) {
        BloodOrb orb = stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (orb == null) return 0;
        return orb.fluidCapacity();
    }

    public static int fillInternal(ItemStack stack, FluidStack fluid, FluidAction action) {
        if (fluid.isEmpty() || fluid.getFluid() != NVFluids.ESSENTIA_VITAE_SOURCE.get()) return 0;

        int capacity = getOrbFluidCapacity(stack);
        if (capacity <= 0) return 0;

        SimpleFluidContent current = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int currentAmount = current.isEmpty() ? 0 : current.getAmount();
        int space = capacity - currentAmount;
        int toFill = Math.min(space, fluid.getAmount());

        if (toFill <= 0) return 0;

        if (action.execute()) {
            FluidStack newFluid = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), currentAmount + toFill);
            stack.set(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.copyOf(newFluid));
        }

        return toFill;
    }

    public static FluidStack drainInternal(ItemStack stack, int amount, FluidAction action) {
        if (amount <= 0) return FluidStack.EMPTY;

        SimpleFluidContent current = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        if (current.isEmpty()) return FluidStack.EMPTY;

        int currentAmount = current.getAmount();
        int toDrain = Math.min(currentAmount, amount);

        if (toDrain <= 0) return FluidStack.EMPTY;

        FluidStack drained = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), toDrain);

        if (action.execute()) {
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
