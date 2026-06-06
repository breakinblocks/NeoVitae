// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.meteor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * A RandomBlockContainer that always returns the same static block.
 */
public class StaticBlockContainer extends RandomBlockContainer {

    private final Block block;

    public StaticBlockContainer(Block block) {
        this.block = block;
    }

    @Override
    public Block getRandomBlock(RandomSource rand, Level level) {
        return block;
    }

    @Override
    public String getEntry() {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    public Block getBlock() {
        return block;
    }
}
