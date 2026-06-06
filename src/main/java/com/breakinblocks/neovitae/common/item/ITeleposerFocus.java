// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.common.datacomponent.Binding;

import java.util.List;

public interface ITeleposerFocus {
    AABB getEntityRangeOffset(Level world, BlockPos teleposerPos);

    List<BlockPos> getBlockListOffset(Level world);

    BlockPos getStoredPos(ItemStack stack);

    Level getStoredWorld(ItemStack stack, Level world);

    Binding getBinding(ItemStack stack);
}
