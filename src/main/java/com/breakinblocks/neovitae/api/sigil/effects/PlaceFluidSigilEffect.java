package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.function.Supplier;

/**
 * Sigil effect that places fluids in the world or fills fluid containers.
 * Used by Water Sigil and Lava Sigil.
 */
public record PlaceFluidSigilEffect(Fluid fluid, int amount) implements SigilEffect {
    public static final MapCodec<PlaceFluidSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(PlaceFluidSigilEffect::fluid),
            Codec.INT.optionalFieldOf("amount", 1000).forGetter(PlaceFluidSigilEffect::amount)
    ).apply(instance, PlaceFluidSigilEffect::new));

    public static final Supplier<MapCodec<PlaceFluidSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("place_fluid", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return false;
        }

        HitResult rayTrace = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (rayTrace == null || rayTrace.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockHitResult blockRayTrace = (BlockHitResult) rayTrace;
        BlockPos blockPos = blockRayTrace.getBlockPos();
        Direction sideHit = blockRayTrace.getDirection();
        BlockPos targetPos = blockPos.relative(sideHit);

        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(targetPos, sideHit, stack)) {
            return false;
        }

        FluidStack fluidStack = new FluidStack(fluid, amount);

        // Try to insert into a tank first
        IFluidHandler destination = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, null);
        if (destination != null && tryInsertFluid(destination, fluidStack, false)) {
            tryInsertFluid(destination, fluidStack, true);
            return true;
        }

        // Try with side
        IFluidHandler destinationSide = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, sideHit);
        if (destinationSide != null && tryInsertFluid(destinationSide, fluidStack, false)) {
            tryInsertFluid(destinationSide, fluidStack, true);
            return true;
        }

        // Place fluid in world
        if (destination == null && destinationSide == null) {
            return tryPlaceFluid(player, level, targetPos, fluidStack);
        }

        return false;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        if (level.isClientSide) {
            return false;
        }

        BlockPos targetPos = blockPos.relative(side);

        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(targetPos, side, stack)) {
            return false;
        }

        FluidStack fluidStack = new FluidStack(fluid, amount);

        // Try to insert into a tank first
        IFluidHandler destination = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, null);
        if (destination != null && tryInsertFluid(destination, fluidStack, false)) {
            tryInsertFluid(destination, fluidStack, true);
            return true;
        }

        // Try with side
        IFluidHandler destinationSide = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, side);
        if (destinationSide != null && tryInsertFluid(destinationSide, fluidStack, false)) {
            tryInsertFluid(destinationSide, fluidStack, true);
            return true;
        }

        // Place fluid in world at the adjacent position
        if (destination == null && destinationSide == null) {
            return tryPlaceFluid(player, level, targetPos, fluidStack);
        }

        return false;
    }

    private boolean tryInsertFluid(IFluidHandler destination, FluidStack fluidStack, boolean doTransfer) {
        if (destination == null || fluidStack.isEmpty()) {
            return false;
        }
        return destination.fill(fluidStack, doTransfer ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE) > 0;
    }

    private boolean tryPlaceFluid(Player player, Level level, BlockPos blockPos, FluidStack fluidStack) {
        if (level == null || blockPos == null || fluidStack.isEmpty()) {
            return false;
        }

        Fluid fluid = fluidStack.getFluid();
        BlockState targetState = level.getBlockState(blockPos);

        // Check if the position is valid for placing fluid
        if (!targetState.canBeReplaced(fluid)) {
            return false;
        }

        // Handle nether vaporization
        if (level.dimensionType().ultraWarm() && fluid.getFluidType().isVaporizedOnPlacement(level, blockPos, fluidStack)) {
            fluid.getFluidType().onVaporize(player, level, blockPos, fluidStack);
            return true;
        }

        // Place the fluid (with protection check)
        if (fluid instanceof FlowingFluid flowingFluid) {
            BlockState fluidState = flowingFluid.getSource().defaultFluidState().createLegacyBlock();
            return BlockProtectionHelper.tryPlaceBlock(level, blockPos, fluidState, player, 11);
        }

        return false;
    }
}
