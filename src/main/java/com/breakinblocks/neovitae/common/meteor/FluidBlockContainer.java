package com.breakinblocks.neovitae.common.meteor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

/**
 * A RandomBlockContainer that returns the block form of a fluid.
 */
public class FluidBlockContainer extends RandomBlockContainer {

    private final Fluid fluid;

    public FluidBlockContainer(Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public Block getRandomBlock(RandomSource rand, Level level) {
        BlockState state = fluid.defaultFluidState().createLegacyBlock();
        if (state == null) {
            return null;
        }
        return state.getBlock();
    }

    @Override
    public String getEntry() {
        ResourceLocation rl = BuiltInRegistries.FLUID.getKey(fluid);
        return ";" + rl.toString();
    }

    public Fluid getFluid() {
        return fluid;
    }
}
