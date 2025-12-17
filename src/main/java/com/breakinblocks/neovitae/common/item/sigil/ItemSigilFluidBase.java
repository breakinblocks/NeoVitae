package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;

/**
 * Base class for sigils that interact with fluids.
 * Handles both creating and deleting fluids in the world.
 */
public abstract class ItemSigilFluidBase extends ItemSigilBase {

    public final FluidStack sigilFluid;

    public ItemSigilFluidBase(String name, int lpUsed, FluidStack fluid) {
        super(name, lpUsed);
        this.sigilFluid = fluid;
    }

    public ItemSigilFluidBase(String name, FluidStack fluid) {
        super(name);
        this.sigilFluid = fluid;
    }

    public ItemSigilFluidBase(String name) {
        super(name);
        this.sigilFluid = FluidStack.EMPTY;
    }

    /**
     * Tries to insert fluid into a fluid handler.
     *
     * @param destination The fluid handler to insert into
     * @param doTransfer  Whether to actually perform the transfer
     * @return Whether the transfer was successful
     */
    protected boolean tryInsertSigilFluid(IFluidHandler destination, boolean doTransfer) {
        if (destination == null || sigilFluid.isEmpty()) {
            return false;
        }
        return destination.fill(sigilFluid, doTransfer ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE) > 0;
    }

    /**
     * Tries to remove fluid from a fluid handler.
     *
     * @param source     The fluid handler to drain from
     * @param amount     The amount to drain
     * @param doTransfer Whether to actually perform the transfer
     * @return Whether the transfer was successful
     */
    protected boolean tryRemoveFluid(IFluidHandler source, int amount, boolean doTransfer) {
        if (source == null) {
            return false;
        }
        FluidStack drained = source.drain(amount, doTransfer ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);
        return !drained.isEmpty();
    }

    /**
     * Tries to place a fluid block in the world.
     *
     * @param player   The player placing the fluid
     * @param world    The world
     * @param blockPos The position to place the fluid
     * @return Whether the placement was successful
     */
    protected boolean tryPlaceSigilFluid(Player player, Level world, BlockPos blockPos) {
        if (world == null || blockPos == null || sigilFluid.isEmpty()) {
            return false;
        }

        Fluid fluid = sigilFluid.getFluid();
        BlockState targetState = world.getBlockState(blockPos);

        // Check if the position is valid for placing fluid
        if (!targetState.canBeReplaced(fluid)) {
            return false;
        }

        // Handle nether vaporization
        if (world.dimensionType().ultraWarm() && fluid.getFluidType().isVaporizedOnPlacement(world, blockPos, sigilFluid)) {
            fluid.getFluidType().onVaporize(player, world, blockPos, sigilFluid);
            return true;
        }

        // Place the fluid (with protection check)
        if (fluid instanceof FlowingFluid flowingFluid) {
            BlockState fluidState = flowingFluid.getSource().defaultFluidState().createLegacyBlock();
            return BlockProtectionHelper.tryPlaceBlock(world, blockPos, fluidState, player, 11);
        }

        return false;
    }

    /**
     * Gets the fluid handler at the given position.
     *
     * @param world    The world
     * @param blockPos The position
     * @param side     The side to check (can be null)
     * @return The fluid handler, or null if none exists
     */
    @Nullable
    protected IFluidHandler getFluidHandler(Level world, BlockPos blockPos, @Nullable Direction side) {
        // In NeoForge 1.21, fluid handlers are accessed via capabilities
        // For now, we'll handle block-based fluids directly
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() instanceof LiquidBlock) {
            // Liquid blocks don't expose fluid handlers the same way
            return null;
        }

        // Try to get capability from block entity
        var blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity != null) {
            var cap = world.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, blockPos, side);
            return cap;
        }

        return null;
    }
}
