// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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

    protected boolean tryInsertSigilFluid(IFluidHandler destination, boolean doTransfer) {
        if (destination == null || sigilFluid.isEmpty()) {
            return false;
        }
        return destination.fill(sigilFluid, doTransfer ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE) > 0;
    }

    protected boolean tryRemoveFluid(IFluidHandler source, int amount, boolean doTransfer) {
        if (source == null) {
            return false;
        }
        FluidStack drained = source.drain(amount, doTransfer ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);
        return !drained.isEmpty();
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

        if (world.dimensionType().ultraWarm() && fluid.getFluidType().isVaporizedOnPlacement(world, blockPos, sigilFluid)) {
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
    protected IFluidHandler getFluidHandler(Level world, BlockPos blockPos, @Nullable Direction side) {
        BlockState state = world.getBlockState(blockPos);
        if (state.getBlock() instanceof LiquidBlock) {
            return null;
        }

        var blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity != null) {
            var cap = world.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, blockPos, side);
            return cap;
        }

        return null;
    }
}
