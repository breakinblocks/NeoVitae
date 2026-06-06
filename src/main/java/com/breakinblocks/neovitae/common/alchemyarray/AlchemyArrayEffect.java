// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;

public abstract class AlchemyArrayEffect {
    private int evCost;

    public int getEvCost() {
        return evCost;
    }

    public void setEvCost(int evCost) {
        this.evCost = evCost;
    }

    public abstract AlchemyArrayEffect getNewCopy();

    public abstract void readFromNBT(CompoundTag compound);

    public abstract void writeToNBT(CompoundTag compound);

    public abstract boolean update(AlchemyArrayBlockEntity array, int activeCounter);

    public void onEntityCollidedWithBlock(AlchemyArrayBlockEntity tile, Level world, BlockPos pos, BlockState state, Entity entity) {
    }

    /**
     * Called when a block adjacent to the array changes. Effects that maintain
     * a neighbour cache can override this to invalidate it reactively instead
     * of waiting for the next timed rebuild.
     */
    public void onNeighborChanged(AlchemyArrayBlockEntity tile, BlockPos neighborPos) {
    }

    /**
     * Invoked when a player right-clicks an active array. Returning true
     * indicates the effect consumed the interaction; the block will report
     * success and skip its default slot-fill logic.
     */
    public boolean onUse(AlchemyArrayBlockEntity tile, Player player) {
        return false;
    }

    public int getRedstoneSignal(AlchemyArrayBlockEntity tile) {
        return 0;
    }

    public boolean isSignalSource() {
        return false;
    }
}
