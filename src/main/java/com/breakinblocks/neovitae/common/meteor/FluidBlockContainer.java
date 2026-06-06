// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.meteor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
        Identifier rl = BuiltInRegistries.FLUID.getKey(fluid);
        return ";" + rl.toString();
    }

    public Fluid getFluid() {
        return fluid;
    }
}
