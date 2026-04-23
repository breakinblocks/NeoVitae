package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;

/**
 * Base class for sigils that interact with fluids.
 * Handles both creating and deleting fluids in the world.
 */
public abstract class ItemSigilFluidBase extends ItemSigilBase {

    public final FluidStack sigilFluid;

    public ItemSigilFluidBase(Item.Properties props, String name, int lpUsed, FluidStack fluid) {
        super(props, name, lpUsed);
        this.sigilFluid = fluid;
    }

    public ItemSigilFluidBase(Item.Properties props, String name, FluidStack fluid) {
        super(props, name);
        this.sigilFluid = fluid;
    }

    public ItemSigilFluidBase(Item.Properties props, String name) {
        super(props, name);
        this.sigilFluid = FluidStack.EMPTY;
    }

    protected boolean tryInsertSigilFluid(ResourceHandler<FluidResource> destination, boolean doTransfer) {
        if (destination == null || sigilFluid.isEmpty()) {
            return false;
        }
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = destination.insert(FluidResource.of(sigilFluid), sigilFluid.getAmount(), tx);
            if (doTransfer) tx.commit();
            return inserted > 0;
        }
    }

    protected boolean tryRemoveFluid(ResourceHandler<FluidResource> source, int amount, boolean doTransfer) {
        if (source == null) return false;
        for (int tank = 0; tank < source.size(); tank++) {
            FluidResource r = source.getResource(tank);
            if (r.isEmpty()) continue;
            try (Transaction tx = Transaction.openRoot()) {
                int drained = source.extract(tank, r, amount, tx);
                if (drained > 0) {
                    if (doTransfer) tx.commit();
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean tryPlaceSigilFluid(Player player, Level world, BlockPos blockPos) {
        if (world == null || blockPos == null || sigilFluid.isEmpty()) {
            return false;
        }

        Fluid fluid = sigilFluid.getFluid();
        BlockState targetState = world.getBlockState(blockPos);

        if (!targetState.canBeReplaced(fluid)) {
            return false;
        }

        if (fluid.getFluidType().isVaporizedOnPlacement(world, blockPos, sigilFluid)) {
            fluid.getFluidType().onVaporize(player, world, blockPos, sigilFluid);
            return true;
        }

        if (fluid instanceof FlowingFluid flowingFluid) {
            BlockState fluidState = flowingFluid.getSource().defaultFluidState().createLegacyBlock();
            return BlockProtectionHelper.tryPlaceBlock(world, blockPos, fluidState, player, 11);
        }

        return false;
    }

    @Nullable
    protected ResourceHandler<FluidResource> getFluidHandler(Level world, BlockPos blockPos, @Nullable Direction side) {
        return world.getCapability(Capabilities.Fluid.BLOCK, blockPos, side);
    }
}
