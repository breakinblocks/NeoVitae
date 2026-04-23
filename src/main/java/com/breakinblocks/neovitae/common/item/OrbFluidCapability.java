package com.breakinblocks.neovitae.common.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.fluid.NVFluids;

public final class OrbFluidCapability extends ItemAccessResourceHandler<FluidResource> {

    public OrbFluidCapability(ItemAccess access) {
        super(access, 1);
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
        SimpleFluidContent content = accessResource.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        if (content.isEmpty()) return FluidResource.EMPTY;
        return FluidResource.of(content.copy());
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        SimpleFluidContent content = accessResource.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        return content.isEmpty() ? 0 : content.getAmount();
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        if (newAmount <= 0 || newResource.isEmpty()) {
            return accessResource.without(NVDataComponents.ORB_FLUID);
        }
        FluidStack stack = newResource.toStack(newAmount);
        return accessResource.with(NVDataComponents.ORB_FLUID, SimpleFluidContent.copyOf(stack));
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return resource.isEmpty() || resource.getFluid() == NVFluids.ESSENTIA_VITAE_SOURCE.get();
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        BloodOrb orb = capacityOf(itemAccess.getResource().toStack(1));
        return orb == null ? 0 : orb.fluidCapacity();
    }

    private static BloodOrb capacityOf(ItemStack stack) {
        return stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
    }
}
