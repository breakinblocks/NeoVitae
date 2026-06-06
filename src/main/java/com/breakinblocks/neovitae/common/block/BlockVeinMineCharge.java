// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.VeinMineChargeBlockEntity;

public class BlockVeinMineCharge extends BlockShapedExplosive {
    private final int maxBlocks;

    public BlockVeinMineCharge(int maxBlocks, Properties properties) {
        super(1, properties); // explosionSize not used for veinmine, just pass 1
        this.maxBlocks = maxBlocks;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VeinMineChargeBlockEntity(maxBlocks, pos, state);
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }
}
